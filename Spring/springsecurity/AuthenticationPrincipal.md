https://blog.locotor.cn/posts/68e9fd0.html

## 通过 SecurityContextHolder 获取

在每次请求中 Spring Security 会自动创建一个验证上下文，通过它可以获取当前用户信息，这个上下文可以通过 `SecurityContextHolder.getContext()` 静态方法来获取。

所以，在任意网络请求中，可以通过这个方式获取当前用户：

```
Authentication auth = (User)SecurityContextHolder.getContext().getAuthentication().

if(auth != null){
    User currentUser  = auth.getPrincipal();
}
```

这个 `User` 类通常是 `UserDetails` 的具体实现。

如果当前验证上下文不存在，`getContext` 方法会返回一个空白的上下文对象。因此需要验证一下当前请求的验证情况。

## 通过 @AuthenticationPrincipal 简化

对于这个常见的需求，Spring Security 提供了一个方便的注解 `@AuthenticationPrincipal`，通过它可以快速获取当前请求用户，如果该用户尚未验证，则返回 null。
使用示例：

```
@GetMapping("getTeam")
public ResponseEntity<ApiResponse> getTeam(@AuthenticationPrincipal User currentUser, String id) {
    ... // 略过业务代码
}
```

## 原理

org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver通过这个类解析。

