# ibauthentication-root

These modules exist primarily to supply credentials to a `ProcessRunner`.

## How this works

You can either inject or query for an `IBAuthenticationProducerFactory` instance, using SPI or Sisu/Plexus.

You will need to set the temp path (where the authentication files will be written).  Some providers allow you to build an environment set instead.

### Configuration of Mappers

The mappers are configured from a selection of unencrypted data provided by processing a `List<IBAuthentication>` items passed into the factory.

This list is processed locally and the authentication data is passed in via `factory.setAuthentications(`.

### Configuring the `Authentication` instances

A `DefaultIBAuthentication` is the default implementation of `IBAuthentication`.

In the default implementation , id is a random UUID string.  Type and ServerId are required.

`type` can be set to the mapping type specified by this instance.  `target` is the generic term to mean what equates to a "region" in AWS
(a thing with availability zones that is seperate from others of its kind).  `serverId` is used to map to an `<id/>` in a `settings.xml` file, in the `<servers/>` section.

`principal` and `secret` are (1) the principal identifier and (2) the "password" or secret.

### Using the Component

You can then call one of the following methods on your `IBAuthenticationProducerFactory` instance:

```
  Map<String,String> getEnvironmentForTypes(List<String> types);
  Map<String,String> getEnvironmentForAllTypes();

```
One will provide the written "environment map" for the types provided, and the other will try to create the map for every single type available (this might fail).

Note that each provider has a different way of providing the environment (or not providing it), and that the file will be configured according to the
`IBAuthentication` instances provided to the factory.

###  Example

In the `imaging-maven-plugin`, there is a config element `authConfig`, a container for `IBAuthentication` items.

```
          <authConfig>
          <!-- Where I write the files -->
            <temp>${project.build.directory}/secret-temp</temp>
            <authentications>
              <authentication>
                <id>github</id>
                <type>amazon-ebs</type>
                <target>us-west-2</target>
                <serverId>github-us-west2</serverId>
              </authentication>
            </authentications>
          </authConfig>
```

In this instance, since the `imaging-maven-plugin` will execute the `setServers` call described above for you, as well as the `getEnvironmentForTypes(` call prior to executing, the data in the `IBAuthentication` gets populated from the user's local `settings.xml`


Basically, it's looking for a match of `<serverId>` to the `<id>` of a `<server>`.  So the following might be the `<servers>` section of your local settings file:

```
<servers>
    <server>
      <id>github-us-west2</id>
      <username>myUserName</username>
      <password>myPassword</password> -->
    </server>
</servers>
```

Since the `<id>` of a server matched, the principal is set to `myUserName` and the secret is set to `myPassword`.


** If nothing matches, then the auth DAO simply won't be updated and the written file could be incorrect. **




