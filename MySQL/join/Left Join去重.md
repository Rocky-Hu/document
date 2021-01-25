~~~
# 第一种写法

explain SELECT
	  distinct(tcgm.group_id),
      tcgm.we_chat_id,
      tcg.group_name,
      tcg.group_type,
      tcg.profile_image_url AS group_profile_image_url,
      tcg.owner_id AS group_owner_id,
      twa.alias AS group_owner_nick_name,
      twa.nick_name AS group_owner_alias_name,
      twa.profile_image_url AS group_owner_profile_image_url,
      twasa.server_we_chat_id AS server_wechat_id
FROM
      tb_chat_group_member tcgm
      JOIN tb_chat_group tcg ON tcgm.group_id = tcg.group_id
      JOIN tb_wechat_account twa ON tcg.owner_id = twa.client_we_chat_id
      LEFT JOIN tb_wechat_account_server_account twasa ON tcg.owner_id = twasa.client_we_chat_id
WHERE
      tcgm.we_chat_id = '1688853802834044'
      AND tcgm.deleted = '0'
      AND tcg.deleted = '0'
      AND tcg.id_group_chat='1'
      AND twa.deleted = '0'
ORDER BY
      tcgm.create_time DESC
~~~

~~~
# 第二种写法

explain SELECT
	tt.*,
	( SELECT server_we_chat_id FROM tb_wechat_account_server_account twasa WHERE tt.group_owner_id = twasa.client_we_chat_id LIMIT 1 ) server_wechat_id 
FROM
	(
	SELECT
		tcgm.we_chat_id,
		tcgm.group_id,
		tcg.group_name,
		tcg.group_type,
		tcg.profile_image_url AS group_profile_image_url,
		tcg.owner_id AS group_owner_id,
		twa.alias AS group_owner_nick_name,
		twa.nick_name AS group_owner_alias_name,
		twa.profile_image_url AS group_owner_profile_image_url 
	FROM
		tb_chat_group_member tcgm
		JOIN tb_chat_group tcg ON tcgm.group_id = tcg.group_id
		JOIN tb_wechat_account twa ON tcg.owner_id = twa.client_we_chat_id 
	WHERE
		tcgm.we_chat_id = '1688853802834044' 
		AND tcgm.deleted = '0' 
		AND tcg.deleted = '0' 
		AND tcg.id_group_chat = '1' 
	AND twa.deleted = '0' 
	) tt;
~~~

