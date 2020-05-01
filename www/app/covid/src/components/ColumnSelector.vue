<template>
    <div>
        <div class="selector" >
            <i class="far" :class="{'fa-check-square':flgDisplay,'fa-square':!flgDisplay}" @click="toggleDisplay" :title="title" > 
                    <label style="margin-left:10px;">{{label}}</label>:
            </i>
        </div>
        <div class="small-modal" v-if="flgDisplay">
            <ul>
                <li v-for="col in colDefs" :key="col.colName">{{col.header}} <input type="checkbox" :checked="!(col.hidden)" @click="toggleHidden(col)"/></li>
            </ul>
        </div>
    </div>
</template>
<script>
export default {
    name: "ColumnSelector",
    props: { colDefs: Array,
             title: String,
             label: String
            },
    data: function(){
        return{
            flgDebug: true,
            flgDisplay: false
        }
    },
    created(){
        // console.log(this.colDefs);
    },
    watch:{
        colDefs(newVal){
            console.log(newVal);
        }
    },
    methods:{
        isVisible(col){
            return !col.hidden;
        },
        toggleDisplay(){
            this.flgDisplay = !this.flgDisplay;
        },
        toggleHidden(col){
            col.hidden = !col.hidden;
        }
    }
}
</script>