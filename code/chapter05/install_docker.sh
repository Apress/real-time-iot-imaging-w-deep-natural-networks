#   a. Install the following prerequisites.
sudo apt-get install apt-transport-https ca-certificates software-properties-common -y
#   b. Download and install Docker.
curl -fsSL get.docker.com -o get-docker.sh && sh get-docker.sh
#   c. Give the ‘pi’ user the ability to run Docker.
sudo usermod -aG docker pi
#   d. Import Docker CPG key.
sudo curl https://download.docker.com/linux/raspbian/gpg
#   e. Setup the Docker Repo.
sudo echo "deb https://download.docker.com/linux/raspbian/ stretch stable" >> /etc/apt/sources.list
#   f. Patch and update your Pi.
sudo apt-get update
sudo apt-get upgrade
#   g. Start the Docker service.
systemctl start docker.service
#   h. To verify that Docker is installed and running.
docker info
#    i. You should now some information in regards to versioning, runtime,etc.