# KPS-Maven-Plugin

Currently, the KPS deployment process is manually by inputting it through the api gateway manager dashboard. With this plugin, the KPS data will be stored to csv format and automatic deployment to spesific api gateway instance.

## Deployment

The project can be deployed to a api gateway manager via the plugin.

Just invoke the `kps:deploy` goal within the deployment project and specify the target domain and instance. In the following example it is assumed that the Admin Node Manager is located at `localhost:8090` and `TEST_01` is the instance used. The user `admin` is allowed to deploy KPS and has the password `admin`. The example file to be used is `sample.csv` which is in the `resources` folder and KPS table name is `sample`.

```shell
mvn kps:deploy -Daxway.kps.csvfile=sample.csv -Daxway.kps.tablename=sample -Daxway.kps.instance=TEST_01 -Daxway.anm.user=admin -Daxway.anm.password=admin -Daxway.anm.host=localhost -Daxway.anm.port=8090
```

## Generate Plugin Documentation

If you want to read the detailed documentation, you can run the command below

```shell
mvn clean site
```