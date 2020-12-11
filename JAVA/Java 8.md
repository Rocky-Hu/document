# 取对象集合中的某个属性成集

~~~java
Set<String> senderIdList = dataList.stream().map(ServerMessageSendDTO::getSenderId).collect(Collectors.toSet());
~~~

# 根据某个属性进行分组

~~~java
Map<String, List<ServerMessageSendDTO>> listMap = dataList.stream().collect(Collectors.groupingBy(ServerMessageSendDTO::getSenderId));
~~~

# 去重

~~~java
List<UserDepartmentDTO> userDepartments = new ArrayList<>();
        List<UserDepartmentEntity> userEntities = userDepartmentService.getDepartmentByUserIds(new ArrayList<>(userIds));
        if (!CollectionUtils.isEmpty(userEntities)){
            userEntities = userEntities.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(()
                    -> new TreeSet<>(Comparator.comparing(UserDepartmentEntity::getUserId))), ArrayList::new));
            userDepartments = userEntities.stream().map(UserDepartmentDTO::conventFromEntity).collect(Collectors.toList());
        }
~~~

