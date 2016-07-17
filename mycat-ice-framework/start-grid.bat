start C:\ZeroC\Ice-3.6.2\bin\icegridregistry  --Ice.Config=C:\project\Ice_8\registry.cfg
start C:\ZeroC\Ice-3.6.2\bin\icegridnode --Ice.Config=C:\project\Ice_8\node1.cfg 
ping 127.0.0.1 -n 3 > null
C:\ZeroC\Ice-3.6.2\bin\icegridadmin  -u test -p test --Ice.Default.Locator="IceGrid/Locator:tcp -h localhost -p 4061"

