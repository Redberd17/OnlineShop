<template>
    <h5>{{ shopUnitName }}</h5>
    <button @click="redirectToParentNew(parentId)" class="btn btn-primary" v-show="parentId !== null">Back</button>
    <button @click="deleteCategory()" class="btn btn-primary">Delete category</button>
    <h5 v-show="children.length === 0 && shopUnitType.name === 'Category'">No such products</h5>
    <div id="vitrine" class="card" style="width: 18rem; height: 10rem; --bs-card-border-color: blue;
    --bs-card-color: green; position: center; align-content: center; text-align: center" v-for="child in children"
         v-bind:key="child.id">
        <div class="card-body" v-show="!isShowNotice">
            <h5 class="card-title">{{ child.shopUnitName }}</h5>
            <button @click="redirectToChildNew(child)" class="btn btn-primary" v-show="!(child.children === null)">See
                all products
            </button>
            <router-link :to="{ name: 'detailInformation', params: { id: child.id} }" class="btn btn-primary"
                         v-show="(child.children === null)">See detail information
            </router-link>
        </div>
    </div>
</template>


<script>

    import EmployeeService from '../services/ProductsService';

    export default {
        name: 'ShopUnit',
        data() {
            return {
                id: String,
                parentId: String,
                shopUnitName: String,
                shopUnitType: String,
                price: String,
                children: [],

                currentObject: Object,

                isShowNotice: false,
                modalShow: false,

                responseNameFromController: String,
                responseCodeFromController: String
            }
        },
        methods: {
            getShopUnits(childId) {
                EmployeeService.getProducts(childId).then((response) => {
                    this.$router.push({name: 'products', params: {id: childId, name: response.data.shopUnitName}});
                    this.shopUnitName = response.data.shopUnitName;
                    this.children = response.data.children == null ? [] : response.data.children;
                    this.parentId = response.data.parentId;
                    this.shopUnitType = response.data.shopUnitType;
                    this.price = response.data.price;
                    this.id = response.data.id;
                    EmployeeService.getProducts(response.data.parentId).then((response) => {
                        this.parentName = response.data.shopUnitName;
                    });
                });
            },
            redirectToChildNew(child) {
                this.currentObject = child;
                EmployeeService.getProducts(child.id).then((response) => {
                    this.$router.push({name: 'products', params: {id: child.id, name: response.data.shopUnitName}});
                    this.shopUnitName = response.data.shopUnitName;
                    this.children = response.data.children == null ? [] : response.data.children;
                    this.parentId = response.data.parentId;
                    this.shopUnitType = response.data.shopUnitType;
                    this.price = response.data.price;
                    this.id = response.data.id;
                    EmployeeService.getProducts(response.data.parentId).then((response) => {
                        this.parentName = response.data.shopUnitName;
                    });
                });
            },
            redirectToParentNew(parentId) {
                EmployeeService.getProducts(parentId).then((response) => {
                    if (response.data.parentId != null) {
                        this.$router.push({name: 'products', params: {id: parentId, name: response.data.shopUnitName}});
                        this.shopUnitName = response.data.shopUnitName;
                        this.children = response.data.children == null ? [] : response.data.children;
                        this.parentId = response.data.parentId;
                        this.shopUnitType = response.data.shopUnitType;
                        this.price = response.data.price;
                        this.id = response.data.id;
                        EmployeeService.getProducts(response.data.parentId).then((response) => {
                            this.parentName = response.data.shopUnitName;
                        });
                    } else {
                        this.$router.push({name: 'homePage'});
                    }
                });
            },
            deleteCategory() {
                EmployeeService.deleteShopUnit(this.id).then((response) => {
                    this.goBack();
                    this.responseNameFromController = response.data;
                    this.responseCodeFromController = response.status;
                }).catch((error) => {
                    console.log('catch error');
                    this.responseNameFromController = error.response.data;
                    this.responseCodeFromController = error.response.status;
                });

            },
            goBack() {
                EmployeeService.getProducts(this.parentId).then((response1) => {

                    if (response1.data.parentId == null) {
                        this.$router.push({name: 'homePage'});
                        return;
                    }

                    EmployeeService.getProducts(response1.data.parentId).then((response2) => {
                        this.$router.push({name: 'products', params: {id: this.parentId, name: this.parentName}});

                        this.shopUnitName = this.parentName;
                        this.parentName = response2.data.shopUnitName;
                        this.shopUnitType = response1.data.shopUnitType;
                        this.price = response1.data.price;
                        this.id = this.parentId;
                        this.parentId = response1.data.parentId;
                        this.children = response1.data.children;
                    });
                });
            }
        },
        created() {
            this.shopUnitName = '';
            this.parentName = '';
            this.shopUnitType = '';
            this.price = '';
            this.responseNameFromController = '';
            this.responseCodeFromController = '';
            this.id = this.$route.params.id;
            this.getShopUnits(this.id);
            this.responseNameFromController = '';
            this.responseCodeFromController = '';
        }

    }

</script>

<style>
    #vitrine {
        display: grid;
        grid-template-columns: 1fr 1fr 1fr 1fr;
        gap: 1em;

        padding: 10px 20px;
    }

    @media only screen and (max-width: 1800px) {
        #vitrine {
            grid-template-columns: 200px 200px;
            gap: 0.5em;

            padding: 5px;
            margin-bottom: 20px;
        }
    }

    .modal-mask {
        position: fixed;
        z-index: 9998;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-color: rgba(0, 0, 0, 0.5);
        display: table;
        transition: opacity 0.3s ease;
    }

    .modal-wrapper {
        display: table-cell;
        vertical-align: middle;
    }

    .modal-container {
        width: 300px;
        margin: 0px auto;
        padding: 20px 30px;
        background-color: #fff;
        border-radius: 2px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
        transition: all 0.3s ease;
    }

    .modal-header h3 {
        margin-top: 0;
        color: #42b983;
    }

    .modal-body {
        margin: 20px 0;
    }

    .modal-default-button {
        float: right;
    }

    /*
     * The following styles are auto-applied to elements with
     * transition="modal" when their visibility is toggled
     * by Vue.js.
     *
     * You can easily play with the modal transition by editing
     * these styles.
     */

    .modal-enter-from {
        opacity: 0;
    }

    .modal-leave-to {
        opacity: 0;
    }

    .modal-enter-from .modal-container,
    .modal-leave-to .modal-container {
        -webkit-transform: scale(1.1);
        transform: scale(1.1);
    }
</style>