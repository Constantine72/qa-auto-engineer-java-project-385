.PHONY: start test


start:
	docker run --rm -p 5173:5173 hexletprojects/qa_auto_java_testing_kanban_board_project_ru_app

test:
	APP_BASE_URL=http://localhost:5173 ./gradlew clean test

