# Implementation Plan - Notes App with Room, Animations, and Gestures

This plan outlines the steps to implement a Note-taking application with Room database persistence, Jetpack Compose UI animations, and swipe-to-delete gestures.

## User Review Required

> [!IMPORTANT]
> - **Gestures**: The requirement mentions `Modifier.swipeable`, which is deprecated. I will use the modern `SwipeToDismissBox` component for the "swipe to delete" functionality.
> - **Date Handling**: `LocalDateTime` will be used as requested. A Room `TypeConverter` will be implemented to store it as a String or Long in SQLite.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/gaste/AndroidStudioProjects/Act4/gradle/libs.versions.toml)
- Add Room dependencies and KSP plugin versions.

#### [MODIFY] [build.gradle.kts (app)](file:///C:/Users/gaste/AndroidStudioProjects/Act4/app/build.gradle.kts)
- Apply KSP plugin.
- Add Room runtime and compiler dependencies.

---

### Data Layer (Room)

#### [NEW] [Note.kt](file:///C:/Users/gaste/AndroidStudioProjects/Act4/app/src/main/java/com/github/indigogal/act3/data/Note.kt)
- Define the `Note` entity with `id`, `title`, `content`, and `date` (LocalDateTime).

#### [NEW] [NoteDao.kt](file:///C:/Users/gaste/AndroidStudioProjects/Act4/app/src/main/java/com/github/indigogal/act3/data/NoteDao.kt)
- Define DAO interface with `getAll()` (Flow) and `insert()`.

#### [NEW] [Converters.kt](file:///C:/Users/gaste/AndroidStudioProjects/Act4/app/src/main/java/com/github/indigogal/act3/data/Converters.kt)
- Room TypeConverters for `LocalDateTime`.

#### [NEW] [AppDatabase.kt](file:///C:/Users/gaste/AndroidStudioProjects/Act4/app/src/main/java/com/github/indigogal/act3/data/AppDatabase.kt)
- Room database class.

---

### Domain & UI Layer

#### [NEW] [NoteViewModel.kt](file:///C:/Users/gaste/AndroidStudioProjects/Act4/app/src/main/java/com/github/indigogal/act3/ui/NoteViewModel.kt)
- ViewModel to manage notes using Coroutines and StateFlow.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/gaste/AndroidStudioProjects/Act4/app/src/main/java/com/github/indigogal/act3/MainActivity.kt)
- Update entry point to use `NoteViewModel`.
- Implement `NotesScreen` with `AnimatedVisibility` for adding notes and `SwipeToDismissBox` for deletion.

---

### UI Components

#### [NEW] [NoteComponents.kt](file:///C:/Users/gaste/AndroidStudioProjects/Act4/app/src/main/java/com/github/indigogal/act3/ui/NoteComponents.kt)
- `NoteCard` component.
- `SwipeToDeleteContainer` to wrap `NoteCard`.

## Verification Plan

### Automated Tests
- Room DAO unit tests (optional but recommended if environment allows).

### Manual Verification
1. **Persistence**: Add a note, close the app, and verify it persists on restart.
2. **Animations**: Observe `slideIn` and `fadeIn` when a new note appears.
3. **Gestures**: Swipe a note to the side to delete it.
