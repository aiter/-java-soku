<%@ page contentType="text/html; charset=utf-8" language="java" %>

<script type="text/javascript">
    window.nova_init_hook_ab = function(){
        Nova.addScript("http://html.atm.youku.com/html?p=280,282,404,405,406,492,618,714&k=<%= WebUtils.urlEncode(webParam.getQ()) %>");
    }
</script>
