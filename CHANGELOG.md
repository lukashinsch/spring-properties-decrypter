# Changelog

## 0.1.9
- Provide information on what property cannot be decrypted (fixes [#8](https://github.com/lukashinsch/spring-properties-decrypter/issues/8))

## 0.1.8
- Bugfix: application listener was not working for most types of property sources (fixes [#4](https://github.com/lukashinsch/spring-properties-decrypter/issues/4))

## 0.1.7
- Switched from PropertySourcesPlaceholderConfigurer to ApplicationListener to avoid possible conflicts

## 0.1.6
- propertyDecryption.prefix no longer removes itself (fixes [#2](https://github.com/lukashinsch/spring-properties-decrypter/issues/2))

## 0.1.5
- Change default prefix to {encrypted}

## 0.1.4
- Make algorithm configurable
- Don't use salt generator to decrypt passwords

## 0.1.3
- Relaxed spring dependency requirements a bit

## 0.1.2
- Clean up naming (decryptor -> decrypter)

## 0.1.1
- Add spring boot auto configuration

## 0.1.0
- Initial release