HOST_IP    = $$(ifconfig en0 | grep inet | grep -v inet6 | awk '{print $$2}')
MYSQL_PORT = $$(HOST_IP=$$HOST_IP docker-compose port mysql 3306 | awk -F':' '{print $$2}')
APP_ENV    ?= dev

GIT_SHA    = $$(git rev-parse --short HEAD)

install:
	sed -e "s/{{HOST_IP}}/\"$(HOST_IP)\"/" config.sample.edn > dev/resources/config.edn;
	sed -e "s/{{HOST_IP}}/$(HOST_IP)/" nginx.conf > tmp/nginx/nginx.conf;
	sed -e "s/{{HOST_IP}}/$(HOST_IP)/" my.cnf > tmp/mysql/my.cnf;

mysql:
	MYSQL_PWD=password mysql -u huey_$(APP_ENV)_user --port $(MYSQL_PORT) --protocol tcp huey_$(APP_ENV)

uberjar:
	lein do clean, test;
	lein with-profile prod uberjar;

build:
	@read -p "Enter Docker Repo:" REPO; \
	echo "$$REPO/huey-$(GIT_SHA)"; \
	docker build -t $$REPO/huey:$(GIT_SHA) .; \
	docker push $$REPO/huey:$(GIT_SHA);

tag:
	@read -p "Enter Docker Repo:" REPO; \
	read -p "Enter Version Tag:" TAG; \
	echo "$$REPO/huey-$$TAG"; \
	echo "$$REPO/huey-$(GIT_SHA)"; \
	docker tag $$REPO/huey:$(GIT_SHA) $$REPO/huey:$$TAG; \
	docker push $$REPO/huey:$$TAG;

deploy:
	@read -p "Enter Docker Repo:" REPO; \
	read -p "Enter Version Tag / git SHA:" TAG; \
	ssh huey-lightsail "\
            docker pull $$REPO/huey:$$TAG && \
            docker kill huey_server && \
            docker run -d --name=huey_server  \
	      --rm --net=bridge \
              -p 3000:3000 \
              $$REPO/huey:$$TAG";
