package com.jrprofessor.serenity.domain.analyzer

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlin.math.sqrt

data class FaceAnalysisState(
    val isFaceDetected: Boolean = false,
    val isFaceAligned: Boolean = false,
    val smilingProbability: Float = 0f,
    val faceScore: Int = 50,
    val faceLabel: String = "detecting...",
    val faceEmoji: String = "🔍",
    val confidence: Float = 0f,
    val guidanceMessage: String = "Position your face in the oval"
)

class EmotionFaceAnalyzer(
    private val onStateUpdated: (FaceAnalysisState) -> Unit
) : ImageAnalysis.Analyzer {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .build()
    )

    private var lastAnalyzedTimestamp = 0L

    // Rolling buffer of head yaw values for jitter (anxious) detection
    private val yawBuffer = ArrayDeque<Float>(MAX_YAW_BUFFER)

    companion object {
        private const val MAX_YAW_BUFFER = 12
        // Min samples before we trust the jitter reading
        private const val MIN_JITTER_SAMPLES = 8
        // Std-dev threshold above which we classify as "anxious"
        private const val JITTER_THRESHOLD_DEG = 4.5f
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val currentTimestamp = System.currentTimeMillis()
        // Throttle to ~10 FPS for optimal battery and smooth UI updates
        if (currentTimestamp - lastAnalyzedTimestamp < 100) {
            imageProxy.close()
            return
        }
        lastAnalyzedTimestamp = currentTimestamp

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(inputImage)
                .addOnSuccessListener { faces ->
                    processFaces(faces, inputImage.width, inputImage.height)
                }
                .addOnFailureListener {
                    onStateUpdated(
                        FaceAnalysisState(
                            isFaceDetected = false,
                            guidanceMessage = "Looking for face..."
                        )
                    )
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    private fun processFaces(faces: List<Face>, imageWidth: Int, imageHeight: Int) {
        if (faces.isEmpty()) {
            yawBuffer.clear()
            onStateUpdated(
                FaceAnalysisState(
                    isFaceDetected = false,
                    guidanceMessage = "Position your face in the oval"
                )
            )
            return
        }

        val primaryFace = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() } ?: return

        val smileProb = primaryFace.smilingProbability ?: -1f
        val leftEyeProb = primaryFace.leftEyeOpenProbability ?: 0.5f
        val rightEyeProb = primaryFace.rightEyeOpenProbability ?: 0.5f
        val avgEyeOpen = (leftEyeProb + rightEyeProb) / 2f

        // Head pose angles from ML Kit
        val headPitch = primaryFace.headEulerAngleX   // negative = chin down
        val headYaw = primaryFace.headEulerAngleY     // left/right turn

        // Accumulate yaw for jitter detection
        if (yawBuffer.size >= MAX_YAW_BUFFER) yawBuffer.removeFirst()
        yawBuffer.addLast(headYaw)
        val headYawStdDev = if (yawBuffer.size >= MIN_JITTER_SAMPLES) stdDev(yawBuffer) else 0f

        // Check if face is reasonably centered in frame
        val centerX = primaryFace.boundingBox.centerX().toFloat() / imageWidth
        val centerY = primaryFace.boundingBox.centerY().toFloat() / imageHeight
        val isCentered = centerX in 0.25f..0.75f && centerY in 0.20f..0.80f

        val result = evaluateEmotion(
            smileProb = smileProb,
            avgEyeOpen = avgEyeOpen,
            headPitch = headPitch,
            headYawVariance = headYawStdDev
        )

        val guidance = if (isCentered) "Hold still…" else "Center your face in the guide"

        onStateUpdated(
            FaceAnalysisState(
                isFaceDetected = true,
                isFaceAligned = isCentered,
                smilingProbability = if (smileProb >= 0) smileProb else 0f,
                faceScore = result.score,
                faceLabel = result.label,
                faceEmoji = result.emoji,
                confidence = result.confidence,
                guidanceMessage = guidance
            )
        )
    }

    /**
     * Evaluates 8 emotion states using smile probability, eye-open probability,
     * head pitch, and head-yaw variance (jitter).
     *
     * Emotion tiers:
     *  1. Radiant Joy   😄  88–100  big smile + eyes open
     *  2. Happy         🙂  72–87   moderate smile
     *  3. Content       😌  60–71   slight smile, calm pose
     *  4. Neutral       😐  50–59   expressionless baseline
     *  5. Tired/Sleepy  😴  35–49   eyes nearly closed
     *  6. Sad           😔  20–34   low smile + chin down
     *  7. Anxious       😟  10–19   high yaw jitter + low smile
     *  8. Distressed    😢   5–9    eyes closed + no smile + head down
     */
    private fun evaluateEmotion(
        smileProb: Float,
        avgEyeOpen: Float,
        headPitch: Float,
        headYawVariance: Float
    ): EmotionResult {
        val eyesClosed = avgEyeOpen < 0.30f
        val eyesTired = avgEyeOpen in 0.30f..0.45f
        val chinDown = headPitch < -8f
        val isJittery = headYawVariance > JITTER_THRESHOLD_DEG

        return when {
            // 1. Radiant Joy — big smile + both eyes open
            smileProb >= 0.80f && avgEyeOpen >= 0.65f -> {
                val score = (88 + ((smileProb - 0.80f) / 0.20f * 12)).toInt().coerceIn(88, 100)
                EmotionResult(score, "radiant joy", "😄", smileProb)
            }

            // 2. Happy — moderate smile
            smileProb >= 0.55f && avgEyeOpen >= 0.40f -> {
                val score = (72 + ((smileProb - 0.55f) / 0.25f * 15)).toInt().coerceIn(72, 87)
                EmotionResult(score, "happy", "🙂", smileProb)
            }

            // 3. Content — mild smile, stable pose
            smileProb in 0.25f..0.55f && !chinDown && !isJittery -> {
                val score = (60 + ((smileProb - 0.25f) / 0.30f * 11)).toInt().coerceIn(60, 71)
                EmotionResult(score, "content", "😌", smileProb)
            }

            // 8. Distressed — eyes closed, no smile, head down (checked before Tired/Sad for specificity)
            smileProb < 0.05f && chinDown && eyesClosed -> {
                EmotionResult(7, "distressed", "😢", 0.9f)
            }

            // 5. Tired / Sleepy — eyes nearly closed regardless of smile
            eyesClosed || eyesTired -> {
                val score = if (eyesClosed) {
                    (5 + (avgEyeOpen / 0.30f * 30)).toInt().coerceIn(5, 34)
                } else {
                    (35 + ((avgEyeOpen - 0.30f) / 0.15f * 14)).toInt().coerceIn(35, 49)
                }
                val label = if (eyesClosed) "distressed" else "tired"
                val emoji = if (eyesClosed) "😢" else "😴"
                EmotionResult(score, label, emoji, 1f - avgEyeOpen)
            }

            // 6. Sad — low smile + chin down
            smileProb < 0.15f && chinDown -> {
                val score = (20 + ((0.15f - smileProb) / 0.15f * 14)).toInt().coerceIn(20, 34)
                EmotionResult(score, "sad", "😔", 1f - smileProb)
            }

            // 7. Anxious — high yaw jitter, not smiling
            isJittery && smileProb < 0.25f -> {
                val jitterNorm = ((headYawVariance - JITTER_THRESHOLD_DEG) / 5f).coerceIn(0f, 1f)
                val score = (19 - (jitterNorm * 9).toInt()).coerceIn(10, 19)
                EmotionResult(score, "anxious", "😟", jitterNorm)
            }

            // 4. Neutral — catch-all
            else -> {
                val score = (50 + ((smileProb.coerceIn(0f, 0.25f) / 0.25f) * 9)).toInt().coerceIn(50, 59)
                EmotionResult(score, "neutral", "😐", 0.6f)
            }
        }
    }

    private data class EmotionResult(
        val score: Int,
        val label: String,
        val emoji: String,
        val confidence: Float
    )

    private fun stdDev(values: Collection<Float>): Float {
        if (values.size < 2) return 0f
        val mean = values.average().toFloat()
        val variance = values.sumOf { ((it - mean) * (it - mean)).toDouble() }.toFloat() / values.size
        return sqrt(variance)
    }
}
