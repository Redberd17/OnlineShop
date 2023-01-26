<template>
    <h5>Detail Information</h5>
    <br>
    <span>Имя: </span>
    <span style="font-style: italic">{{ shopUnitName }}</span>
    <br>
    <br>
    <span>Тип продукта: </span>
    <span style="font-style: italic">{{ shopUnitType }}</span>
    <br>
    <br>
    <span>Стоимость продукта: </span>
    <span style="font-style: italic">{{ price }}</span>
    <br>
    <br>
    <span>Категория: </span>
    <span style="font-style: italic">{{ parentName }}</span>
    <br>
    <br>
    <router-link :to="{ name: 'changeProduct', params: { id: id} }" class="btn btn-primary">Change product</router-link>

    <button @click="deleteProduct()" class="btn btn-primary">Delete product</button>
    <button @click="goBack()" class="btn btn-primary">Back</button>

</template>


<script>

    import EmployeeService from '../services/ProductsService';

    export default {
        name: 'ShopUnitDetails',
        data() {
            return {
                id: String,

                shopUnitName: String,
                shopUnitType: String,
                parentName: String,
                parentId: String,
                price: Number,

                currentObject: Object,

                categories: [],
                categoriesId: [],

                responseNameFromController: String,
                responseCodeFromController: String
            }
        },
        methods: {
            getChangeProduct() {
                Promise.all([

                    EmployeeService.getProducts(this.id).then((response) => {
                        this.shopUnitName = response.data.shopUnitName;
                        this.shopUnitType = response.data.shopUnitType.name;
                        this.price = response.data.price;
                        this.parentId = response.data.parentId;
                        EmployeeService.getProducts(response.data.parentId).then((response) => {
                            this.parentName = response.data.shopUnitName;
                        });
                    })

                ]);
            },
            getParentName(parentId) {
                EmployeeService.getProducts(parentId).then((response) => {
                    this.parentName = response.data.shopUnitName;
                });
            },
            getAllCategories() {
                EmployeeService.getAllCategories().then((response) => {
                    for (let i = 0; i < response.data.length; i++) {
                        this.categoriesId.push(response.data[i].id);
                        this.categories.push(response.data[i].name);
                    }
                });
            },
            deleteProduct() {
                const name = this.parentName;

                function isEquals(element) {
                    return element === name;
                }

                const index = this.categories.findIndex(isEquals);
                this.parentId = this.categoriesId[index];

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
                this.$router.push({name: 'products', params: { id: this.parentId, name: this.parentName }});
            }
        },
        created() {
            this.id = this.$route.params.id;

            this.shopUnitName = '';
            this.parentName = '';
            this.parentId = '';
            this.shopUnitType = '';
            this.price = '';
            this.responseNameFromController = '';
            this.responseCodeFromController = '';

            this.getChangeProduct();
            this.getAllCategories();

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
</style>