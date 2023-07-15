export DOCKER_DEFAULT_PLATFORM=linux/amd64

docker build -t $HARBOR_URL/tusdesign/jeepay-deps:$TAG -f docs/Dockerfile .
docker push $HARBOR_URL/tusdesign/jeepay-deps:$TAG
