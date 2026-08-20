package com.jrprofessor.serenity.domain.analyzer

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

data class FaceAnalysisState(
    val isFaceDetected: Boolean = false,
    val isFaceAligned: Boolean = false,
    val smilingProbability: Float = 0f,
    val faceScore: Int = 50,
    val faceLabel: String = "detecting...",
    val guidanceMessage: String = "Position your face in the oval"
)

class EmotionFaceAnalyzer(
    private val onStateUpdated: (FaceAnalysisState) -> Unit
) : ImageAnalysis.Analyzer {

    private val detector = FaceDetection.getClient(
        FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
    )

    private var lastAnalyzedTimestamp = 0L

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

        // Check if face is reasonably centered in frame
        val centerX = primaryFace.boundingBox.centerX().toFloat() / imageWidth
        val centerY = primaryFace.boundingBox.centerY().toFloat() / imageHeight
        val isCentered = centerX in 0.25f..0.75f && centerY in 0.20f..0.80f

        // Compute faceScore (0-100) and emotion label
        val (score, label) = evaluateEmotion(smileProb, avgEyeOpen)

        val guidance = if (isCentered) "Hold still…" else "Center face in guide"

        onStateUpdated(
            FaceAnalysisState(
                isFaceDetected = true,
                isFaceAligned = isCentered,
                smilingProbability = if (smileProb >= 0) smileProb else 0f,
                faceScore = score,
                faceLabel = label,
                guidanceMessage = guidance
            )
        )
    }

    private fun evaluateEmotion(smileProb: Float, avgEyeOpen: Float): Pair<Int, String> {
        return when {
            smileProb >= 0.70f -> {
                val score = (75 + (smileProb * 25)).toInt().coerceIn(75, 100)
                score to "radiant smile"
            }
            smileProb in 0.35f..0.70f -> {
                val score = (60 + (smileProb * 20)).toInt().coerceIn(60, 75)
                score to "gentle smile"
            }
            smileProb in 0.10f..0.35f -> {
                val score = 55 to "mostly calm"
                score
            }
            smileProb in 0.0f..0.10f && avgEyeOpen < 0.35f -> {
                val score = 38 to "tired eyes"
                score
            }
            smileProb in 0.0f..0.10f -> {
                val score = 42 to "mostly sad"
                score
            }
            else -> {
                50 to "thoughtful"
            }
        }
    }
}
