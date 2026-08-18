.PHONY: help install clean test run debug format lint hooks db-up db-down up down

.DEFAULT_GOAL := help

MVN ?= mvn
DEBUG_PORT ?= 8787
DEBUG_OPTS ?= -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:$(DEBUG_PORT)

# Spotless/Lombok on JDK 23+; JDK 21 (CI) rejects this option.
JAVA_SPEC_VERSION := $(shell java -XshowSettings:properties -version 2>&1 | awk -F= '/java.specification.version/ { gsub(/[[:space:]]/, "", $$2); print $$2; exit }')
ifeq ($(shell test "$(JAVA_SPEC_VERSION)" -ge 23 >/dev/null 2>&1 && echo yes),yes)
export MAVEN_OPTS += --sun-misc-unsafe-memory-access=allow
endif

##@ Usage
help: ## Display this help message
	@awk 'BEGIN {FS = ":.*?## "} \
		/^##@/ { printf "\n\033[1m%s\033[0m\n", substr($$0, 5) } \
		/^[a-zA-Z_-]+:.*?## / { printf "  \033[36m%-12s\033[0m %s\n", $$1, $$2 }' $(MAKEFILE_LIST)

##@ Build
install: ## Clean build and install to local Maven repo
	$(MVN) clean install

clean: ## Remove build output
	$(MVN) clean

##@ Quality
format: ## Apply Spotless formatting
	$(MVN) spotless:apply

lint: ## Check Spotless formatting
	$(MVN) spotless:check

test: ## Run unit tests
	$(MVN) test

hooks: ## Install Git hooks from .githooks
	./scripts/install_hooks.sh

##@ App
run: ## Start the app (expects Postgres on localhost:5432)
	$(MVN) spring-boot:run

debug: ## Start the app with JDWP on port 8787 (override with DEBUG_PORT=...)
	$(MVN) spring-boot:run -Dspring-boot.run.jvmArguments="$(DEBUG_OPTS)"

##@ Docker
db-up: ## Start Postgres
	docker compose up -d postgres

db-down: ## Stop Postgres and remove the data volume
	docker compose down -v

up: ## Build and start the API and Postgres
	docker compose up --build

down: ## Stop all Compose services
	docker compose down
