HOST_IP    = $$(ifconfig en0 | grep inet | grep -v inet6 | awk '{print $$2}')
MYSQL_PORT = $$(HOST_IP=$$HOST_IP docker-compose port mysql 3306 | awk -F':' '{print $$2}')
APP_ENV    ?= dev

mysql:
	MYSQL_PWD=password mysql -u huey_$(APP_ENV)_user --port $(MYSQL_PORT) --protocol tcp huey_$(APP_ENV)
