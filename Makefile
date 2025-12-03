.PHONY: help start restart stop clean rcon logs

DOCKER_COMPOSE := docker compose -f docker/compose.yml

start:
	./gradlew shadowJar
	./docker/download-plugins.sh
	$(DOCKER_COMPOSE) up

restart:
	$(DOCKER_COMPOSE) restart minecraft

stop:
	$(DOCKER_COMPOSE) down

clean:
	$(DOCKER_COMPOSE) down -v

rcon:
	@$(DOCKER_COMPOSE) exec minecraft rcon-cli

logs:
	@$(DOCKER_COMPOSE) logs -f minecraft
