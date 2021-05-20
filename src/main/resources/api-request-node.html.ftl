<h2 id="${requestNode.methodName}"><a href="#">${(requestNode.description)!''} <#if requestNode.deprecated><span
                class="badge">过期</span></#if></a></h2>
<#if requestNode.supplement??>
    <p class="text-muted">${requestNode.supplement}</p>
</#if>
<#if requestNode.author??>
    <p class="text-muted"><em>作者: ${requestNode.author}</em></p>
</#if>
<p><strong>请求URL</strong></p>
<p>
    <code>${requestNode.url}</code>
    <#list requestNode.method as method>
        <span class="label label-default">${method}</span>
    </#list>
    <#if requestNode.changeFlag == 1>
        <span class="label label-success">新增</span>
    <#elseif requestNode.changeFlag == 2>
        <span class="label label-warning">修改</span>
    </#if>
</p>
<#if requestNode.paramNodes?size != 0>
    <#assign requestJsonBody = ''/>
    <#list requestNode.paramNodes as paramNode>
        <#if paramNode.jsonBody>
            <#assign requestJsonBody = paramNode.description/>
        </#if>
    </#list>
    <#if requestJsonBody == '' || (requestJsonBody != '' && requestNode.paramNodes?size gt 1)>
        <p><strong>请求参数</strong> <span class="badge">application/x-www-form-urlencoded</span></p>
        <table class="table table-bordered">
            <tr>
                <th>参数名</th>
                <th>类型</th>
                <th>必须</th>
                <th>描述</th>
            </tr>
            <#list requestNode.paramNodes as paramNode>
                <#if !(paramNode.jsonBody)>
                    <tr>
                        <td>${paramNode.name}</td>
                        <td>${paramNode.type}</td>
                        <td>${paramNode.required?string('是','否')}</td>
                        <td>${(paramNode.description)!''}</td>
                    </tr>
                </#if>
            </#list>
        </table>
    </#if>
    <#if requestJsonBody != ''>
        <p><strong>请求体</strong> <span class="badge">application/json</span></p>
        <pre class="prettyprint lang-json">${requestJsonBody}</pre>
    </#if>
</#if>
<#if requestNode.responseNode??>
    <p><strong>返回结果</strong></p>
    <pre class="prettyprint lang-json">${requestNode.responseNode.toJsonApi()}</pre>
    <#if requestNode.androidCodePath??>
        <div class="form-group">
            <a type="button" class="btn btn-sm btn-default" href="${requestNode.androidCodePath}"><i
                        class="fa fa-android" aria-hidden="true"></i> Android Model</a>
        </div>
    </#if>
</#if>
