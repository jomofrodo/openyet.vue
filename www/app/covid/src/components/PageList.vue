<template>
    <div>
        <router-link v-for="page in pages" :key="page.path" :to="page.path">{{page.name}}</router-link>
    </div>
</template>
<script>
export default {
    data: function(){
        return{
            pages:[
                {
                name: "Nation (static)",
                path: "/nation/static" 
                },
                { name: "Nation (dynamic)",
                  path: "/nation"
                }
            ]

            
        }
    }
}
</script>