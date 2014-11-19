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
while read -r line; do
    export "$line"
done < user-data

# Download the code via RELEASE_URL - which is supplied by user-data. We
# expect the tar ball to include a single directory in which the app's
# code and 'setup.sh' are located.
rm -rf app
mkdir app
curl -L $RELEASE_URL | tar xvz -C app

BUILD_VERSION=$(ls app)
cd app/"$BUILD_VERSION"

chmod +x ./setup.sh
./setup.sh
