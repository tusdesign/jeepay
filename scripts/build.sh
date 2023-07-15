export DOCKER_DEFAULT_PLATFORM=linux/amd64

docker buildx build . --build-arg PORT=9216 --build-arg PLATFORM=payment -t $HARBOR_URL/tusdesign/jeepay-payment:$TAG
docker push $HARBOR_URL/tusdesign/jeepay-payment:$TAG
docker buildx build . --build-arg PORT=9217 --build-arg PLATFORM=manager -t $HARBOR_URL/tusdesign/jeepay-manager:$TAG
docker push $HARBOR_URL/tusdesign/jeepay-manager:$TAG
docker buildx build . --build-arg PORT=9218 --build-arg PLATFORM=merchant -t $HARBOR_URL/tusdesign/jeepay-merchant:$TAG
docker push $HARBOR_URL/tusdesign/jeepay-merchant:$TAG

