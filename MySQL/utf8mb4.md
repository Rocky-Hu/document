[UTF-8](https://en.wikipedia.org/wiki/UTF-8) is a variable-length encoding. In the case of UTF-8, this means that storing one code point requires one to four bytes. However, MySQL's encoding called "utf8" (alias of "utf8mb3") only stores a maximum of three bytes per code point.

So the character set "utf8"/"utf8mb3" cannot store all Unicode code points: it only supports the range 0x000 to 0xFFFF, which is called the "[Basic Multilingual Plane](http://en.wikipedia.org/wiki/Plane_(Unicode)#Basic_Multilingual_Plane)". See also [Comparison of Unicode encodings](http://en.wikipedia.org/wiki/Comparison_of_Unicode_encodings#In_detail).

This is what (a previous version of the same page at) [the MySQL documentation](https://dev.mysql.com/doc/refman/5.5/en/charset-unicode-utf8mb4.html) has to say about it:

> The character set named utf8[/utf8mb3] uses a maximum of three bytes per character and contains only BMP characters. As of MySQL 5.5.3, the utf8mb4 character set uses a maximum of four bytes per character supports supplemental characters:
>
> - For a BMP character, utf8[/utf8mb3] and utf8mb4 have identical storage characteristics: same code values, same encoding, same length.
> - For a supplementary character, **utf8[/utf8mb3] cannot store the character at all**, while utf8mb4 requires four bytes to store it. Since utf8[/utf8mb3] cannot store the character at all, you do not have any supplementary characters in utf8[/utf8mb3] columns and you need not worry about converting characters or losing data when upgrading utf8[/utf8mb3] data from older versions of MySQL.

So if you want your column to support storing characters lying outside the BMP (and you usually want to), such as [emoji](https://en.wikipedia.org/wiki/Emoji), use "utf8mb4". See also [What are the most common non-BMP Unicode characters in actual use?](https://stackoverflow.com/questions/5567249/what-are-the-most-common-non-bmp-unicode-characters-in-actual-use).

# 排序规则

utf8mb4对应的排序字符集有utf8mb4_unicode_ci、utf8mb4_general_ci。

utf8mb4_unicode_ci和utf8mb4_general_ci的对比：

准确性：

- utf8mb4_unicode_ci是基于标准的Unicode来排序和比较，能够在各种语言之间精确排序
- utf8mb4_general_ci没有实现Unicode排序规则，在遇到某些特殊语言或者字符集，排序结果可能不一致。
但是，在绝大多数情况下，这些特殊字符的顺序并不需要那么精确。

性能：

- utf8mb4_general_ci在比较和排序的时候更快

- utf8mb4_unicode_ci在特殊情况下，Unicode排序规则为了能够处理特殊字符的情况，实现了略微复杂的排序算法。

  但是在绝大多数情况下发，不会发生此类复杂比较。相比选择哪一种collation，使用者更应该关心字符集与排序规则在db里需要统一。