```
docker build -t myfips .

docker network create mysql

# Set up certs
openssl req -x509 -newkey rsa:4096 -keyout ca.key -out ca.crt -days 365 -subj "/CN=myca"  -nodes -addext keyUsage=critical,digitalSignature,keyCertSign

openssl req -new -nodes -out mysql.csr -newkey rsa:4096 -keyout mysql.key -subj '/CN=mysqls'
openssl x509 -req -in mysql.csr -CA ca.crt -CAkey ca.key -out mysql.crt -days 365

docker run --network mysql \
-e MYSQL_USER=user \
-e MYSQL_PASSWORD=password \
-e MYSQL_DATABASE=database \
-e MYSQL_ALLOW_EMPTY_PASSWORD=1 \
--name mysqls --rm -d \
-v $PWD/my.cnf:/etc/my.cnf \
-v $PWD:/certs \
cgr.dev/chainguard-private/mysql
```

The default will use TLS and not validate certs:
```
docker run --network mysql -e JDBC=jdbc:mysql://mysqls:3306/database -e DB_USER=user -e DB_PASSWORD=password myfips

NOTE: Picked up JDK_JAVA_OPTIONS: --add-exports=java.base/sun.security.internal.spec=ALL-UNNAMED --add-exports=java.base/sun.security.provider=ALL-UNNAMED -Djavax.net.ssl.trustStoreType=FIPS
Picked up JAVA_TOOL_OPTIONS: --module-path=/usr/share/java/bouncycastle-fips
Apr 21, 2026 3:32:28 PM org.bouncycastle.jsse.provider.PropertyUtils getBooleanSecurityProperty
INFO: Found boolean security property [keystore.type.compat]: true
Apr 21, 2026 3:32:28 PM org.bouncycastle.jsse.provider.PropertyUtils getStringSecurityProperty
INFO: Found string security property [jdk.tls.disabledAlgorithms]: MD5, SSLv3, TLSv1, TLSv1.1, RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 224, 3DES_EDE_CBC, anon, NULL, secp112r1, secp112r2, secp128r1, secp128r2, secp160k1, secp160r1, secp160r2, secp192k1, secp192r1, secp224k1, secp224r1, secp256k1, sect113r1, sect113r2, sect131r1, sect131r2, sect163k1, sect163r1, sect163r2, sect193r1, sect193r2, sect233k1, sect233r1, sect239k1, sect283k1, sect283r1, sect409k1, sect409r1, sect571k1, sect571r1, brainpoolP256r1, brainpoolP320r1, brainpoolP384r1, brainpoolP512r1
Apr 21, 2026 3:32:28 PM org.bouncycastle.jsse.provider.PropertyUtils getStringSecurityProperty
INFO: Found string security property [jdk.certpath.disabledAlgorithms]: MD2, MD5, RSA keySize < 1024, DSA keySize < 1024, EC keySize < 224, SHA1, secp112r1, secp112r2, secp128r1, secp128r2, secp160k1, secp160r1, secp160r2, secp192k1, secp192r1, secp224k1, secp224r1, secp256k1, sect113r1, sect113r2, sect131r1, sect131r2, sect163k1, sect163r1, sect163r2, sect193r1, sect193r2, sect233k1, sect233r1, sect239k1, sect283k1, sect283r1, sect409k1, sect409r1, sect571k1, sect571r1, brainpoolP256r1, brainpoolP320r1, brainpoolP384r1, brainpoolP512r1
Successfully connected to the database!
Query result: 1
```

We can require that same config:
```
docker run --network mysql -e 'JDBC=jdbc:mysql://mysqls:3306/database?useSSL=true&verifyServerCertificate=false' -e DB_USER=user -e DB_PASSWORD=password myfips 
NOTE: Picked up JDK_JAVA_OPTIONS: --add-exports=java.base/sun.security.internal.spec=ALL-UNNAMED --add-exports=java.base/sun.security.provider=ALL-UNNAMED -Djavax.net.ssl.trustStoreType=FIPS
Picked up JAVA_TOOL_OPTIONS: --module-path=/usr/share/java/bouncycastle-fips
Apr 21, 2026 3:45:31 PM org.bouncycastle.jsse.provider.PropertyUtils getBooleanSecurityProperty
INFO: Found boolean security property [keystore.type.compat]: true
Apr 21, 2026 3:45:31 PM org.bouncycastle.jsse.provider.PropertyUtils getStringSecurityProperty
INFO: Found string security property [jdk.tls.disabledAlgorithms]: MD5, SSLv3, TLSv1, TLSv1.1, RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 224, 3DES_EDE_CBC, anon, NULL, secp112r1, secp112r2, secp128r1, secp128r2, secp160k1, secp160r1, secp160r2, secp192k1, secp192r1, secp224k1, secp224r1, secp256k1, sect113r1, sect113r2, sect131r1, sect131r2, sect163k1, sect163r1, sect163r2, sect193r1, sect193r2, sect233k1, sect233r1, sect239k1, sect283k1, sect283r1, sect409k1, sect409r1, sect571k1, sect571r1, brainpoolP256r1, brainpoolP320r1, brainpoolP384r1, brainpoolP512r1
Apr 21, 2026 3:45:31 PM org.bouncycastle.jsse.provider.PropertyUtils getStringSecurityProperty
INFO: Found string security property [jdk.certpath.disabledAlgorithms]: MD2, MD5, RSA keySize < 1024, DSA keySize < 1024, EC keySize < 224, SHA1, secp112r1, secp112r2, secp128r1, secp128r2, secp160k1, secp160r1, secp160r2, secp192k1, secp192r1, secp224k1, secp224r1, secp256k1, sect113r1, sect113r2, sect131r1, sect131r2, sect163k1, sect163r1, sect163r2, sect193r1, sect193r2, sect233k1, sect233r1, sect239k1, sect283k1, sect283r1, sect409k1, sect409r1, sect571k1, sect571r1, brainpoolP256r1, brainpoolP320r1, brainpoolP384r1, brainpoolP512r1
Successfully connected to the database!
Query result: 1
```

Or try to NOT use SSL or not validate the server cert (it is self signed)
```
docker run --network mysql -e 'JDBC=jdbc:mysql://mysqls:3306/database?useSSL=false&verifyServerCertificate=false' -e DB_USER=user -e DB_PASSWORD=password myfips
NOTE: Picked up JDK_JAVA_OPTIONS: --add-exports=java.base/sun.security.internal.spec=ALL-UNNAMED --add-exports=java.base/sun.security.provider=ALL-UNNAMED -Djavax.net.ssl.trustStoreType=FIPS
Picked up JAVA_TOOL_OPTIONS: --module-path=/usr/share/java/bouncycastle-fips
Database connection failed!
java.sql.SQLException: Connections using insecure transport are prohibited while --require_secure_transport=ON.
	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:130)
	at com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping.translateException(SQLExceptionsMapping.java:122)
....


docker run --network mysql -e 'JDBC=jdbc:mysql://mysqls:3306/database?useSSL=true&verifyServerCertificate=true' -e DB_USER=user -e DB_PASSWORD=password myfips
NOTE: Picked up JDK_JAVA_OPTIONS: --add-exports=java.base/sun.security.internal.spec=ALL-UNNAMED --add-exports=java.base/sun.security.provider=ALL-UNNAMED -Djavax.net.ssl.trustStoreType=FIPS
Picked up JAVA_TOOL_OPTIONS: --module-path=/usr/share/java/bouncycastle-fips
Apr 21, 2026 3:47:56 PM org.bouncycastle.jsse.provider.PropertyUtils getBooleanSecurityProperty
INFO: Found boolean security property [keystore.type.compat]: true
Apr 21, 2026 3:47:56 PM org.bouncycastle.jsse.provider.PropertyUtils getStringSystemProperty
INFO: Found string system property [java.home]: /usr/lib/jvm/java-17-openjdk
Apr 21, 2026 3:47:56 PM org.bouncycastle.jsse.provider.PropertyUtils getStringSystemProperty
INFO: Found string system property [javax.net.ssl.trustStoreType]: FIPS
Apr 21, 2026 3:47:56 PM org.bouncycastle.jsse.provider.PropertyUtils getStringSecurityProperty
INFO: Found string security property [jdk.tls.disabledAlgorithms]: MD5, SSLv3, TLSv1, TLSv1.1, RC4, DES, MD5withRSA, DH keySize < 1024, EC keySize < 224, 3DES_EDE_CBC, anon, NULL, secp112r1, secp112r2, secp128r1, secp128r2, secp160k1, secp160r1, secp160r2, secp192k1, secp192r1, secp224k1, secp224r1, secp256k1, sect113r1, sect113r2, sect131r1, sect131r2, sect163k1, sect163r1, sect163r2, sect193r1, sect193r2, sect233k1, sect233r1, sect239k1, sect283k1, sect283r1, sect409k1, sect409r1, sect571k1, sect571r1, brainpoolP256r1, brainpoolP320r1, brainpoolP384r1, brainpoolP512r1
Apr 21, 2026 3:47:56 PM org.bouncycastle.jsse.provider.PropertyUtils getStringSecurityProperty
INFO: Found string security property [jdk.certpath.disabledAlgorithms]: MD2, MD5, RSA keySize < 1024, DSA keySize < 1024, EC keySize < 224, SHA1, secp112r1, secp112r2, secp128r1, secp128r2, secp160k1, secp160r1, secp160r2, secp192k1, secp192r1, secp224k1, secp224r1, secp256k1, sect113r1, sect113r2, sect131r1, sect131r2, sect163k1, sect163r1, sect163r2, sect193r1, sect193r2, sect233k1, sect233r1, sect239k1, sect283k1, sect283r1, sect409k1, sect409r1, sect571k1, sect571r1, brainpoolP256r1, brainpoolP320r1, brainpoolP384r1, brainpoolP512r1
Apr 21, 2026 3:47:56 PM org.bouncycastle.jsse.provider.ProvTlsClient notifyAlertRaised
INFO: [client #1 @1f992a3a] raised fatal(2) certificate_unknown(46) alert: Failed to read record
org.bouncycastle.tls.TlsFatalAlert: certificate_unknown(46)
	at org.bouncycastle.fips.tls/org.bouncycastle.jsse.provider.ProvSSLSocketWrap.checkServerTrusted(ProvSSLSocketWrap.java:132)
	at org.bouncycastle.fips.tls/org.bouncycastle.jsse.provider.ProvTlsClient$1.notifyServerCertificate(ProvTlsClient.java:385)
	at org.bouncycastle.fips.tls/org.bouncycastle.tls.TlsUtils.processServerCertificate(TlsUtils.java:4923)
	at org.bouncycastle.fips.tls/org.bouncycastle.tls.TlsClientProtocol.handleServerCertificate(TlsClientProtocol.java:799)
.....
```

