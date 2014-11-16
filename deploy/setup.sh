#!/bin/sh
# Abort our setup if anything goes wrong.
set -e

# Our upstart script redirects all output of this script to /var/log/app-setup.log
# We can debug the output looking for when the script started.
echo "at=start-setup time=`date`"

# We are making use of EC2's user data feature. When you provision a new
# EC2 instance, you can supply arbitrary text as the user data. In the case
# our setup script, we are assuming that the data in the user data store is
# valid bash. Specifically we are expecting that it contain variable declerations
# for $RELEASE_URL and $APP
curl http://169.254.169.254/latest/user-data -o user-data
export $(cat user-data)

# We are passing all of user-dat to the app as well
cat user-data > app-env

# Download the code via RELEASE_URL - which is supplied by user-data. We
# expect the tar ball to include a single directory in which the app's
# code and Dockerfile are located.
rm -rf build
mkdir build
curl -L $RELEASE_URL | tar xvz -C build
BUILD_VERSION=$(ls build)

#Even though our upstart script calls this setup program after the docker
# service is started, it still can be possible for the docker service to
# not be fully operational. This step handle that race condition.
until docker version; do
  echo 'waiting for docker'
  sleep 1
done

BUILD_USER=${USER:-root}
# Build the app based on the Dockerfile.
docker build -t "$BUILD_USER/$BUILD_VERSION" "build/$BUILD_VERSION" 2>&1

# We now use docker to run the container. We are doing a little hack here
# to get our environment variables into the conatiner. We can't specify
# our environment variables in the Dockerfile since the file is in version
# control. (No secrets in the source!) So, we take the result of
# user-data, massage it into the -e flags that the docker run command
# expects.
docker run $(cat app-env | awk '{print "-e " $1}' | tr -s '\n' ' ') -d "$BUILD_USER/$BUILD_VERSION" 2>&1