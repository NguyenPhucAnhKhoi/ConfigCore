# ConfigCore
```
<repository>
  <id>jitpack.io</id>
	<url>https://jitpack.io</url>
</repository>
```
```
<dependency>
  <groupId>com.github.NguyenPhucAnhKhoi</groupId>
  <artifactId>ConfigCore</artifactId>
  <version>Tag</version>
</dependency>
```
# Example
> How to use config core ?
```
//Create the file config instance
FileConfig file = new FileConfig("filePath");
//Get an object from path. The string will match with the closest if not found
Object o = file.get("path");
//You also can get a string or another type by config core
String s = file.getString("path");
Integer i = file.getInt("path");
//In addition, it also supports automatic value conversion by type class
ItemStack stack = file.getObject("path", ItemStack.class);
//Reload the config
 file.reload();
```
