https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-type-conversions.html

| MySQL Type Name                     | Return value of `GetColumnTypeName`                          | Return value of `GetColumnClassName`                         |
| :---------------------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| `BIT(1)`                            | `BIT`                                                        | `java.lang.Boolean`                                          |
| `BIT( > 1)`                         | `BIT`                                                        | `byte[]`                                                     |
| `TINYINT(1) SIGNED, BOOLEAN`        | If `tinyInt1isBit=true` and `transformedBitIsBoolean=false`: `BIT`If `tinyInt1isBit=true` and `transformedBitIsBoolean=true: BOOLEAN`If `tinyInt1isBit=false`: `TINYINT` | If `tinyInt1isBit=true` and `transformedBitIsBoolean=false`:` java.lang.Boolean`If `tinyInt1isBit=true` and `transformedBitIsBoolean=true`:` java.lang.Boolean`If `tinyInt1isBit=false`: `java.lang.Integer` |
| `TINYINT( > 1) SIGNED`              | `TINYINT`                                                    | `java.lang.Integer`                                          |
| `TINYINT( any ) UNSIGNED`           | `TINYINT UNSIGNED`                                           | `java.lang.Integer`                                          |
| `SMALLINT[(M)] [UNSIGNED]`          | `SMALLINT [UNSIGNED]`                                        | `java.lang.Integer` (regardless of whether it is `UNSIGNED` or not) |
| `MEDIUMINT[(M)] [UNSIGNED]`         | `MEDIUMINT [UNSIGNED]`                                       | `java.lang.Integer` (regardless of whether it is `UNSIGNED` or not) |
| `INT,INTEGER[(M)]`                  | `INTEGER`                                                    | `java.lang.Integer`                                          |
| `INT,INTEGER[(M)] UNSIGNED`         | `INTEGER UNSIGNED`                                           | `java.lang.Long`                                             |
| `BIGINT[(M)]`                       | `BIGINT`                                                     | `java.lang.Long`                                             |
| `BIGINT[(M)] UNSIGNED`              | `BIGINT UNSIGNED`                                            | `java.math.BigInteger`                                       |
| `FLOAT[(M,D)]`                      | `FLOAT`                                                      | `java.lang.Float`                                            |
| `DOUBLE[(M,B)] [UNSIGNED]`          | `DOUBLE`                                                     | `java.lang.Double` (regardless of whether it is `UNSIGNED` or not) |
| `DECIMAL[(M[,D])] [UNSIGNED]`       | `DECIMAL`                                                    | `java.math.BigDecimal` (regardless of whether it is `UNSIGNED` or not) |
| `DATE`                              | `DATE`                                                       | `java.sql.Date`                                              |
| `DATETIME`                          | `DATETIME`                                                   | `java.time.LocalDateTime`                                    |
| `TIMESTAMP[(M)]`                    | `TIMESTAMP`                                                  | `java.sql.Timestamp`                                         |
| `TIME`                              | `TIME`                                                       | `java.sql.Time`                                              |
| `YEAR[(2|4)]`                       | `YEAR`                                                       | If `yearIsDateType` configuration property is set to `false`, then the returned object type is `java.sql.Short`. If set to `true` (the default), then the returned object is of type `java.sql.Date` with the date set to January 1st, at midnight. |
| `CHAR(M)`                           | `CHAR`                                                       | `java.lang.String`                                           |
| `VARCHAR(M)`                        | `VARCHAR`                                                    | `java.lang.String`                                           |
| `BINARY(M)`, `CHAR(M) BINARY`       | `BINARY`                                                     | `byte[]`                                                     |
| `VARBINARY(M)`, `VARCHAR(M) BINARY` | `VARBINARY`                                                  | `byte[]`                                                     |
| `BLOB`                              | `BLOB`                                                       | `byte[]`                                                     |
| `TINYBLOB`                          | `TINYBLOB`                                                   | `byte[]`                                                     |
| `MEDIUMBLOB`                        | `MEDIUMBLOB`                                                 | `byte[]`                                                     |
| `LONGBLOB`                          | `LONGBLOB`                                                   | `byte[]`                                                     |
| `TEXT`                              | `TEXT`                                                       | `java.lang.String`                                           |
| `TINYTEXT`                          | `TINYTEXT`                                                   | `java.lang.String`                                           |
| `MEDIUMTEXT`                        | `MEDIUMTEXT`                                                 | `java.lang.String`                                           |
| `LONGTEXT`                          | `LONGTEXT`                                                   | `java.lang.String`                                           |
| `JSON`                              | `JSON`                                                       | `java.lang.String`                                           |
| `GEOMETRY`                          | `GEOMETRY`                                                   | `byte[]`                                                     |
| `ENUM('value1','value2',...)`       | `CHAR`                                                       | `java.lang.String`                                           |
| `SET('value1','value2',...)`        | `CHAR`                                                       | `java.lang.String`                                           |

