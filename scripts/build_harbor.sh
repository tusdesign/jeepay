export TZ=Asia/Shanghai
export BUILD_TIME=$(date +'%Y-%m-%dT%H:%M:%S%z')

export DOCKER_DEFAULT_PLATFORM=linux/amd64
export TAG=oci.tuxm.art:8443/tusdesign/jeepay-payment:latest

# docker build -t jeepay-deps:latest -f docs/Dockerfile_github .

docker buildx build -f Dockerfile_github \
	-t $TAG \
    --build-arg USER=${GITHUB_USER} \
    --build-arg TOKEN=${GITHUB_TOKEN} \
    --build-arg PORT=9216 \
    --build-arg PLATFORM=payment \
    --build-arg BUILD_TIME=${BUILD_TIME} \
    --build-arg VERSION=${VERSION} \
    --build-arg COMMIT_SHA=${COMMIT_SHA} .

docker buildx build -f Dockerfile_github \
 -t oci.tuxm.art:8443/tusdesign/jeepay-manager:latest \
 --build-arg PORT=9217 \
 --build-arg PLATFORM=manager \
 --build-arg BUILD_TIME=${BUILD_TIME} \
 --build-arg VERSION=${VERSION} \
 --build-arg COMMIT_SHA=${COMMIT_SHA} .

docker buildx build -f Dockerfile_github \
 -t oci.tuxm.art:8443/tusdesign/jeepay-merchant:latest \
 --build-arg PORT=9218 \
 --build-arg PLATFORM=merchant \
 --build-arg BUILD_TIME=${BUILD_TIME} \
 --build-arg VERSION=${VERSION} \
 --build-arg COMMIT_SHA=${COMMIT_SHA} .

docker login oci.tuxm.art:8443 --username ${HARBOR_USER} --password ${HARBOR_PASS}
docker push $TAG
docker push oci.tuxm.art:8443/tusdesign/jeepay-manager:latest
docker push oci.tuxm.art:8443/tusdesign/jeepay-merchant:latest

