# Proiect-ISS
# 🏋️ Workout Management System

A robust Java application designed to help users track their fitness journey, manage workout routines, and monitor strength progress over time.

## 📋 Project Overview
This system allows users to create personalized workout plans by selecting exercises from a curated database, logging weekly weights, and visualizing progress through historical comparisons and "Personal Best" milestones.

## 🛠️ Functional Model (UML)
The system's logic is built upon a formal functional model described through **UML Use Case Diagrams**.

### Use Case Diagram Structure
The system differentiates between the **User** (Primary Actor) and the **Admin**.
* **Core CRUD**: Sign-up, Login, and Delete Account.
* **Workout Logic**: Adding exercises filtered by muscle group.
* **Progress Tracking**: Weekly weight logging with "Personal Best" extension logic.

---

## 📖 Use Case Specifications
The project follows the **Cockburn template** for detailed functional descriptions.

### UC-1: Sign-up for an Account
* **Description**: Allows new users to register. It **includes** a check to verify if the user already exists in the database.
* **Preconditions**: User is not currently logged in.

### UC-2 & UC-3: Login and Delete Account
* **Key Feature**: Both use cases **include** a shared `Verify Credentials` process to ensure security and reduce redundancy.

### UC-4: Add Exercises to Workout
* **Flow**: User selects a muscle group → System filters database → User adds selected exercises to the workout.

### UC-5: Add Weight and Track Progress
* **Parallel View**: Displays the weight from the previous week for immediate comparison.
* **PB Message**: An **extension** that displays a "Personal Best!" text on the screen when a record is broken.

### UC-6: Add to Database (Admin)
* **Description**: Allows the Admin to populate the global exercise catalog.

---

## 💻 Technical Implementation (Traceability)
The project is developed in **Java** using **IntelliJ IDEA**, following a clear architecture.

| Use Case | Method-level Trace (Java) |
| :--- | :--- |
| **UC-1: Sign-up** | `com.workout.app.auth.RegistrationController#handleSignUp` |
| **UC-2: Login** | `com.workout.app.auth.LoginController#processLogin` |
| **UC-3: Delete** | `com.workout.app.profile.AccountController#requestDeletion` |
| **UC-4: Add Exercise**| `com.workout.app.controller.WorkoutController#selectMuscleGroup` |
| **UC-5: Progress** | `com.workout.app.controller.WeightController#logWeight` |

---
