import React from "react";
import Categories from "./Categories";
import Products from "./Products";

const Content = () => {
  return (
    <>

      <div className="products-title">
        <h1 className="products">Categories</h1>
      </div>

      <Categories />

      <div className="products-title">
        <h1 className="products">All Products</h1>
      </div>

      <Products />
    </>
  );
};

export default Content;
