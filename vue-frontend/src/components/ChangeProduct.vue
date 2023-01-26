<template>
    <h5>Change Product</h5>
    <br>
    <span>Имя: </span>
    <label>
        <input v-model="shopUnitName" placeholder="Input name of product">
    </label>
    <br>
    <br>
    <span>Тип продукта: </span>
    <label>
        <select v-model="shopUnitType">
            <option disabled value="">Выберите один из вариантов</option>
            <option>Category</option>
            <option>Product</option>
        </select>
    </label>
    <br>
    <br>
    <span>Стоимость продукта: </span>
    <label>
        <input v-model="price" placeholder="Input price of product">
    </label>
    <br>
    <br>
    <span>Категория: </span>
    <label>
        <select v-model="parentName">
            <option disabled value="">Выберите один из вариантов</option>
            <option>Without category</option>
            <option v-for="category in categories" v-bind:key="category.id">{{ category }}</option>
        </select>
    </label>
    <br>
    <br>

    <button @click="saveChanges()" class="btn btn-primary">Save changes</button>

    <router-link :to="{ name: 'detailInformation', params: { id: id} }" class="btn btn-primary">Back</router-link>
    <span></span>


</template>


<script>

    import EmployeeService from '../services/ProductsService';

    export default {
        name: 'ShopUnitChange',
        data() {
            return {
                id: String,

                shopUnitName: String,
                shopUnitType: String,
                parentId: String,
                parentName: String,
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
                EmployeeService.getProducts(this.id).then((response) => {
                    this.shopUnitName = response.data.shopUnitName;
                    this.shopUnitType = response.data.shopUnitType.name;
                    this.price = response.data.price;
                    this.parentId = response.data.parentId;
                    this.getParentName(response.data.parentId);
                });
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
            saveChanges() {
                const name = this.parentName;

                function isEquals(element) {
                    return element === name;
                }

                const index = this.categories.findIndex(isEquals);
                this.parentId = this.categoriesId[index];

                Promise.all([

                    EmployeeService.importProduct(
                        this.id, this.shopUnitName, this.shopUnitType, this.price, this.parentId)
                        .then((response) => {
                            this.responseNameFromController = response.data;
                            this.responseCodeFromController = response.status;
                            this.$router.push({name: 'detailInformation', params: {id: this.id}});
                        }).catch((error) => {
                        console.log('catch error');
                        this.responseNameFromController = error.response.data;
                        this.responseCodeFromController = error.response.status;
                    }),
                ]);

            }
        },
        created() {
            this.id = this.$route.params.id;

            this.shopUnitName = '';
            this.parentId = '';
            this.parentName = '';
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