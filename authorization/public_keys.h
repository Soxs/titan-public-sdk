#pragma once
#include "receipt.h"
namespace titan::authorization::detail {
inline const std::vector<Key>& publicKeys() {
    static const std::vector<Key> keys{
    {"website-20261009-current", R"PEM(-----BEGIN PUBLIC KEY-----
MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAr+LxbtsTTFGInd93z9N/
/pT2xiVYrAQlvxKyLLNvccYKfLDi6XkHbxx0riYpm6Nxl2xM85ydxt0whYGYEqOw
vxzpSr4dyGyEFC06x6ISRg0segnnC6p3njoenk4mec3/oayn9S1KZevZvneQRLlf
OnFrAdW9SFP0atnE4pQ/DBsTOVbdTAww45Eq8TVvZtvrogOJVjclh9DG5bHkI0iR
sx5UEgda0M9WIKhLynkUJmUD8c4sj3CbKPjBZoS5ULbPF85J4Vvt+6KlMUuN3kF1
J76KXTbpzw3ksyE+xm/M5WdEBc8odrjwEN9xDZx7pIyk/Qwg9B4BZlUMzWMf3Xwl
lLHRumREcZXyQKfjXB4yWiDia8h4iJn52QtC8sd5DUkJrsIThNCeJJHRyhx3nxc5
FqJa90RItvesKoMGD3vgVh5CQVzQOcRHCPNir44GQI/PM36P5HevNbavaefaOTt8
3hTWN9vv/44vJ8zJmXBok0E3F7sfWKkEbD6vb7GrjaCvAgMBAAE=
-----END PUBLIC KEY-----)PEM"},
    {"website-20261009-backup", R"PEM(-----BEGIN PUBLIC KEY-----
MIIBojANBgkqhkiG9w0BAQEFAAOCAY8AMIIBigKCAYEAnDWDpd/BJo8Nbt8cVr1g
BMosiV2m5G4mdcZNSclRTqxDdlumjQ8MIkUs/aRW3e3IQCBh93Hp2YubzSMl6ceA
DTLQPIVPOYcpQpeCMCkDc4ZfpOScnG31sgFIKvymy0oUu3yayvH8v3APRV1X05MX
ohsD2XeLzK1ewvsY4eOTJYX1nGCBlsw0euO30ZcZ0s/b+z/44VClDEKSJIUODD32
xD2u0TSnpxqC9008mK8jYaVL2LS1a6/ZkXQ/xS8M4TxFygbsbM3b9o3KrSxnubIh
5fGyJ5AQHX3xuGIVU5niMpw6duD/mlxrIbl5gi54NrBptjvaM8FooELpYpNszLRb
YZMb6RGjaBtrWkN4l1Ju/ClCdxCbPOdtkajJRRIFkHFcicVkdziN+7TFCdCUP3GZ
Hu3VBfB/lG/NlCgwlyahhEQzh5Lu3tiCYgHhLHqYe4mTgFbQDH2hWIsBicOvlTKf
lS1Mdf2wwDApLCft1kqrZnnOWrsPFcYbP+e+dcaXKwrrAgMBAAE=
-----END PUBLIC KEY-----)PEM"}
    };
    return keys;
}
}
