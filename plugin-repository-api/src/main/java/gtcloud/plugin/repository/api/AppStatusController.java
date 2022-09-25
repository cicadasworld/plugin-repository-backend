package gtcloud.plugin.repository.api;

import gtcloud.plugin.repository.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "插件仓库应用状态管理")
@RestController
public class AppStatusController {

    @Operation(summary = "查看插件仓库服务器端应用状态", description = "查看插件仓库服务器端应用状态")
    @GetMapping("/gmx/v1/ping")
    public Result<String> ping() {
        return Result.success("Plugin Repository Server is running");
    }

}
