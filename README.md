### Hexlet tests and linter status:
[![Actions Status](https://github.com/Constantine72/qa-auto-engineer-java-project-385/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/Constantine72/qa-auto-engineer-java-project-385/actions)

# Kanban Board Automation Project

An automated UI testing framework for a Kanban Board

## Tech Stack

* **Language:** Java 11+
* **Tool:** Selenium WebDriver
* **Pattern:** Page Object Model (POM)
* **Framework:** JUnit
* **Build Tool:** Maven

## Architecture

**1. Page Objects
* **`LoginPage.java`:** Handles user authentication, credential inputs, session management
* **`KanbanPage.java` & `TasksPage.java`:** Core pages for board interaction
* **`StatusesPage.java` & `LabelsPage.java`:** Manage board metadata
* **`UsersPage.java`:** Handles assignee data and user management interface

**1. Tests
* **`KanbanTest.java`:** Contains end-to-end test scenarios

## Test Coverage

1. **Filtering**
2. **Editing**
3. **Status Moving**
4. **Deletion**

## How to run

```bash
make test