![Logo of the Project](https://github.com/openfmb/dtech-demo-2016/blob/master/img/openfmb-tm-black_reduced_100.png)

# Description

This is a visualization tool for the dtech-demo.  It subscribes to the MQTT broker for all the topics and generation a web based hmi for the display.  The HMI has logic to sum values and control capabilities. 

# Installing / Getting started

Refer to [Wiki](https://github.com/openfmb/dtech-demo-2016/wiki/Running-the-Simulation) for information on DTech Demo on running and visualizing the dtech demo. 

```shell
java -jar openfmb-hmi-0.0.5-SNAPSHOT-jar-with-dependencies.jar
```

## Building

```shell
git clone https://github.com/openfmb/openfmb-hmi.git
cd openfmb-hmi
mvn clean install
```
The build jar is put in the target directory and needs to be moved to the main directory where the properities files are located. 


## Configuration

The configuration for the HMI is contained in the [hmi.proprities](https://github.com/openfmb/openfmb-hmi/blob/master/hmi.properties) file. In the file the Recloser and Battery are defined so controls can be issued.  The remaining topics listed are so the hmi can subscription and publish to the simulators.


# Contributing

Daniel Evans, Green Energy Corp

If you'd like to contribute, please fork the repository and use a feature
branch. Pull requests are warmly welcome.

Please review the [CONTRIBUTING](https://github.com/openfmb/openfmb-hmi/blob/master/CONTRIBUTING.md) file. 

# License

See the [APACHE_FILE_HEADER](https://github.com/openfmb/openfmb-hmi/blob/master/APACHE_FILE_HEADER) file for more info.
