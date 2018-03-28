# MORF
MegaRAC Open Redfish Framework

### How to build MORF for Project Olympus
* Clone the Project Olympus OpenBMC build files, and the MORF bitbake layers:
```
git clone https://github.com/ProjectOlympusOpenBMC/openbmc.git
git clone https://github.com/ami-megarac/MORF-OpenBMC.git
```
* Add AMI Megarac layers to openbmc:
```
rsync -av MORF-OpenBMC/meta-ami/meta-megarac openbmc/meta-ami
```
* Add the webserver and megarac layers to bblayers.conf.sample:
```
git apply --directory=openbmc MORF-OpenBMC/obmc_patches/bblayers.conf.sample.patch
```
* Add MORF and its dependencies to local.conf.sample:
```
git apply --directory=openbmc MORF-OpenBMC/obmc_patches/local.conf.sample.patch
```
* Set Project Olympus as the build target and build:
```
cd openbmc
export TEMPLATECONF=meta-openbmc-machines/meta-ami/meta-olympus/conf
. openbmc-env
bitbake obmc-phosphor-image
```
