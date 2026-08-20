# Serenity — AI Mood Journal — Implementation Plan

Based on the 4 Stitch-generated screens (app name: **Serenity**). This plan documents exactly what was designed, locks a design system from it, resolves the inconsistencies between screens, and lays out a build plan in phases.

---

## 1. Design Audit (what was actually generated)

### Screen 1 — Mood Check-In (Home)
- Top bar: profile icon (left), title **"Serenity"** (center), settings gear (right)
- Heading: **"How are you feeling today?"**
- Row of 5 mood circles: 😭 😔 😐 🙂 😍 — selected mood (🙂) is enlarged with a soft glow ring, others dimmed/greyscale
- Secondary pill button: **"Scan Your Face Instead"** with camera icon
- Camera preview card: rounded rect, live face inside an oval outline guide, caption **"Hold still…"**
- Privacy note under camera: lock icon + **"Processed on your device — not stored or uploaded."**
- Primary button **"Next"** — greyed/disabled until a mood is set
- Bottom nav: **Home · Mood (active) · Insights · Profile**

### Screen 2 — Journal
- Top bar: back arrow (left), **"Mood: 😔"** chip (center, reflects Screen 1 selection)
- Heading: **"Today was stressful because…"** (dynamic, mood-dependent)
- Large multiline input, placeholder **"Let it all out here…"**
- Bottom-right of input area: character counter **"0 / 1000"** + mic icon button
- Primary button: **"Analyze"**, coral→lavender gradient pill
- No bottom nav visible on this screen (full-focus writing mode)

### Screen 3 — Check-In Result (AI Analysis)
- Top bar: small circular avatar/icon (left), **"Serenity"** title, sparkle/insights icon (right)
- Heading: **"Your Check-In"**
- Chip row: **"Mood: 😔"** and **"Face Reaction: 68/100 — mostly sad"**
- Card **"Stress Level"**: horizontal bar, coral→orange gradient fill, **72%** filled, percentage label right-aligned
- Card **"You seem to be experiencing:"**: bullet list with green ring icons — *Work pressure, Mental fatigue, Low motivation*
- Card **"SUGGESTION"** (small caps label + lightbulb icon): *"Take a 15-minute break and step outside if you can."*
- Primary button: **"Save to Journal"**, coral→lavender gradient, glowing
- Bottom nav: **Mood · Scan (active) · Journal · Guide**

### Screen 4 — History ("Your Journey")
- Heading: **"Your Journey"**, subtext **"Track your emotional landscape over time."**
- Segmented control: **7 Days / 30 Days (active) / 90 Days**
- Card **"Mood Flow"**: smooth glowing line chart with emoji markers at data points, small trend icon top-right
- Streak card: **"🔥 5-day journaling streak"** (green text)
- Section heading: **"Recent Reflections"**
- List of entry cards, each with: date (e.g. "OCT 24"), a status pill (**Stress 72%** pink / **Calm 85%** green / **Stress 55%** pink), mood emoji, truncated journal snippet
- Bottom nav: **Reflect · Meditate · Journal · Insights (active)**

---

## 2. ⚠️ Inconsistency to Resolve: Bottom Navigation

Each generated screen has a **different** bottom nav (this is expected from Stitch generating screens independently):

| Screen | Nav items shown |
|---|---|
| 1 — Mood Check-In | Home · Mood · Insights · Profile |
| 2 — Journal | *(none — focus mode)* |
| 3 — Check-In Result | Mood · Scan · Journal · Guide |
| 4 — History | Reflect · Meditate · Journal · Insights |

**Decision needed before build.** Recommended unified nav (4 tabs, covers everything seen across all screens without redundancy):

```
🏠 Home     🧘 Mood     📓 Journal     📈 Insights
```

- **Home** — dashboard/greeting, streak, quick "Check In" CTA (merges Screen 1's Home tab + Screen 4's Reflect/Meditate concepts as home-screen sections instead of separate tabs)
- **Mood** — entry point into Screen 1 (mood pick / face scan) → Screen 2 (journal) → Screen 3 (result)
- **Journal** — flat list of all past entries (kept from Screens 3 & 4)
- **Insights** — Screen 4's "Your Journey" chart + trends (kept)

"Profile," "Settings," "Guide," and "Meditate" become secondary destinations reached from Home or the top-bar icons, not primary tabs — keeps the nav from getting crowded.

> Confirm this before Phase 2 below, since it affects `NavGraph` structure.

---

## 3. Design System (extracted from screens)

### Color palette
| Token | Usage | Approx. value |
|---|---|---|
| `bg.gradient.start` | Background top | `#12101F` (deep navy-black) |
| `bg.gradient.end` | Background bottom | `#1E1836` (deep purple) |
| `surface.glass` | Card fill | White @ 6–10% opacity over background |
| `border.glass` | Card border | White @ 10–14% opacity, 1px |
| `accent.coral` | Stress/negative, gradient start | `#FF8A80`–`#FF6F61` |
| `accent.lavender` | Gradient end, neutral accents | `#B39DDB`–`#C9A9E9` |
| `accent.mint` | Positive/calm indicators | `#7FE0B4` |
| `accent.yellow` | Mood emoji glow (selected) | `#FFC94A` |
| `text.primary` | Headings | `#F5F3FA` (near white) |
| `text.secondary` | Subtext, placeholders | `#A9A4C0` (muted lavender-grey) |

### Typography
- Rounded, geometric sans-serif (e.g. **Poppins**, **Quicksand**, or **Nunito**) for headings
- Standard system sans (e.g. **Inter**, **Roboto**) for body/input text
- Heading scale: ~28–32sp bold; body ~15–16sp regular; chip/label text ~13sp medium

### Core visual language
- **Glassmorphism cards**: rounded 20–24dp corners, translucent fill, subtle blur, thin light border, soft outer glow on primary actions
- **Gradient pills** (coral→lavender) for all primary CTAs
- **Chips** (rounded, dark translucent) for compact status like `Mood: 😔`
- **Progress bars**: gradient fill, rounded ends, animated fill-in
- **Glow effects**: selected mood emoji and active chart line both use a soft outer glow — a consistent "highlight" motif to reuse everywhere something is "active" or "selected"

---

## 4. Component Inventory to Build

| Component | Used on | Notes |
|---|---|---|
| `TopBar` (variants: logo+icons, back+chip, avatar+icon) | 1, 2, 3 | 3 variants, same height/padding |
| `MoodEmojiSelector` | 1 | 5-item row, glow-on-select animation |
| `PillButton` (secondary, outline) | 1 | "Scan Your Face Instead" style |
| `FaceScanCameraCard` | 1 | Camera preview + oval guide overlay + caption |
| `PrivacyNote` | 1 | Icon + small muted text |
| `GradientPillButton` (primary CTA) | 1, 2, 3 | Disabled vs. active states |
| `MoodChip` | 2, 3 | `Mood: {emoji}` |
| `FaceReactionChip` | 3 | `Face Reaction: {score}/100 — {label}` |
| `GlassCard` | 1, 3, 4 | Base container for all card content |
| `AnimatedStressBar` | 3 | Gradient fill, animated, % label |
| `ThemeBulletList` | 3 | Ring-icon bullet rows |
| `SuggestionCard` | 3 | Icon + label + text |
| `JournalTextField` | 2 | Multiline, counter, mic button |
| `SegmentedControl` | 4 | 7/30/90 day toggle |
| `MoodFlowChart` | 4 | Animated line chart w/ emoji markers |
| `StreakBadge` | 4 | Icon + text pill |
| `ReflectionListItem` | 4 | Date, status pill, emoji, snippet |
| `BottomNavBar` | all | Single reusable component, 4 fixed tabs (see Section 2) |

---

## 5. Tech Stack (unchanged from prior doc — confirmed compatible with this design)

- **UI:** Jetpack Compose (Kotlin) — glassmorphism via `Modifier.blur()` + translucent backgrounds; gradients via `Brush.linearGradient`
- **Camera / face scan:** CameraX + ML Kit Face Detection (on-device)
- **Charts:** custom `Canvas`-drawn line chart (for the glow + emoji-marker look) or Vico as a base, customized
- **Local storage:** Room
- **Networking:** Retrofit + OkHttp → backend proxy → Anthropic API (Claude)
- **Animation:** `animateFloatAsState`, `AnimatedVisibility`, `rememberInfiniteTransition` (for glow/pulse effects)

---

## 6. Build Phases

### Phase 0 — Foundations
- [ ] Confirm unified bottom-nav structure (Section 2)
- [ ] Set up project, add dependencies (Compose, Room, Retrofit, CameraX, ML Kit)
- [ ] Build `theme/Color.kt`, `Type.kt`, `Theme.kt` from Section 3 tokens
- [ ] Build base `GlassCard`, `GradientPillButton`, `PillButton`, `BottomNavBar`

### Phase 1 — Screen 1: Mood Check-In
- [ ] `MoodEmojiSelector` with glow-on-select animation
- [ ] `FaceScanCameraCard` (CameraX preview + oval overlay + "Hold still…" state)
- [ ] Wire ML Kit face detection → derive face score/label (see prior doc, Section 5.5)
- [ ] `PrivacyNote` + camera permission request flow
- [ ] Disabled → active `Next` button logic

### Phase 2 — Screen 2: Journal
- [ ] `MoodChip` in top bar, reflects Screen 1 selection
- [ ] Dynamic heading text based on mood value
- [ ] `JournalTextField` with counter + mic (voice-to-text) button
- [ ] `Analyze` button enable/disable logic (min character count)

### Phase 3 — Backend + AI wiring
- [ ] Stand up `/analyze` endpoint (Node/Firebase Function)
- [ ] Send `{mood, faceScore, faceLabel, journalText}` → Claude → structured JSON
- [ ] Handle loading + error states in-app

### Phase 4 — Screen 3: Check-In Result
- [ ] `MoodChip` + `FaceReactionChip` row
- [ ] `AnimatedStressBar` (animate fill on screen entry)
- [ ] `ThemeBulletList`
- [ ] `SuggestionCard`
- [ ] `Save to Journal` → persist `MoodEntry` to Room

### Phase 5 — Screen 4: Insights / History
- [ ] `SegmentedControl` (7/30/90 days) driving query range
- [ ] `MoodFlowChart` — animated glowing line + emoji markers at data points
- [ ] `StreakBadge` — compute consecutive check-in days
- [ ] `ReflectionListItem` list, tap → read-only detail (reuse Screen 3 layout)

### Phase 6 — Polish & Ship
- [ ] Empty states (no entries yet), loading states, offline handling
- [ ] Accessibility pass (contrast on glass cards, camera fallback for no-camera devices)
- [ ] Test safety fallback path (crisis-language detection in journal text)
- [ ] Build release APK/AAB

---

## 7. Data Model (confirmed against Screen 3 & 4 fields)

```kotlin
data class MoodEntry(
    val id: String,
    val date: Long,
    val mood: Int,                // 0-4, drives emoji
    val moodSource: String,       // "manual" | "face_scan"
    val faceScore: Int?,          // e.g. 68
    val faceLabel: String?,       // e.g. "mostly sad"
    val journalText: String,
    val stressLevel: Int,         // 0-100, drives "Stress 72%" / "Calm 85%" pill on History
    val themes: List<String>,     // e.g. ["Work pressure", "Mental fatigue", "Low motivation"]
    val suggestion: String
)
```

> Note: History screen shows both **"Stress X%"** (pink pill) and **"Calm X%"** (green pill) — implies the same `stressLevel` field is displayed as "Calm" when low (e.g. `stressLevel < 40` → show `100 - stressLevel` as "Calm %" in mint green) and as "Stress" when high. Decide the exact threshold during Phase 5.

---

## 8. Open Questions Before Build
1. Confirm the unified bottom nav (Section 2) — Home / Mood / Journal / Insights?
2. Should "Guide" and "Meditate" (seen on Screens 3 & 4) become real features, or were they Stitch filler — keep, cut, or defer to a later roadmap?
3. Stress vs. Calm pill threshold — where's the cutoff?
4. Face scan: ship with ML Kit heuristic (smiling probability) first, or wait for a trained TFLite emotion model before launch?
