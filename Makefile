.PHONY: help install clean test run debug format lint db-up db-down up down

.DEFAULT_GOAL := help

MVN ?= mvn
DEBUG_PORT ?= 8787
DEBUG_OPTS ?= -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:$(DEBUG_PORT)

help: ## Display this help message
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "\033[36m%-15s\033[0m %s\n", $$1, $$2}' $(MAKEFILE_LIST)

install: ## Clean build and install to local Maven repo
	$(MVN) clean install

clean: ## Remove build output
	$(MVN) clean

test: ## Run unit tests
	$(MVN) test

run: ## Start the app (expects Postgres on localhost:5432)
	$(MVN) spring-boot:run

debug: ## Start the app with JDWP on port 8787 (override with DEBUG_PORT=...)
	$(MVN) spring-boot:run -Dspring-boot.run.jvmArguments="$(DEBUG_OPTS)"

format: ## Apply Spotless formatting
	$(MVN) spotless:apply

lint: ## Check Spotless formatting
	$(MVN) spotless:check

db-up: ## Start Postgres
	docker compose up -d postgres

db-down: ## Stop Postgres and remove the data volume
	docker compose down -v

up: ## Build and start the API and Postgres
	docker compose up --build

down: ## Stop all Compose services
	docker compose down
