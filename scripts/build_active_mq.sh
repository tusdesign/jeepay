export DOCKER_DEFAULT_PLATFORM=linux/amd64
docker build -t $HARBOR_URL/tusdesign/jeepay-activemq:$TAG ./docker/activemq
docker push $HARBOR_URL/tusdesign/jeepay-activemq:$TAG