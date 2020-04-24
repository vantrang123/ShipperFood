package com.trangdv.shipperfood.retrofit;

import com.trangdv.shipperfood.model.MaxOrderModel;
import com.trangdv.shipperfood.model.OrderDetailModel;
import com.trangdv.shipperfood.model.OrderModel;
import com.trangdv.shipperfood.model.RestaurantModel;
import com.trangdv.shipperfood.model.ShipperModel;
import com.trangdv.shipperfood.model.ShippingOrderModel;
import com.trangdv.shipperfood.model.TokenModel;
import com.trangdv.shipperfood.model.UpdateOrderModel;
import com.trangdv.shipperfood.model.UpdateShipperModel;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface IAnNgonAPI {
    @GET("shipper")
    Observable<ShipperModel> getShipper(@Query("key") String apiKey,
                                     @Query("userPhone") String userPhone,
                                     @Query("password") String password);


    @POST("shipper")
    @FormUrlEncoded
    Observable<UpdateShipperModel> updateShipper(@Field("key") String key,
                                               @Field("userPhone") String userPhone,
                                               @Field("userName") String userName,
                                               @Field("userPassword") String userPassword,
                                               @Field("restaurantId") int restaurantId,
                                               @Field("fbid") String fbid);

    @GET("nearbyrestaurant")
    Observable<RestaurantModel> getNearbyRestaurant(@Query("key") String apiKey,
                                                    @Query("lat") Double lat,
                                                    @Query("lng") Double lng,
                                                    @Query("distance") int distance);

    @GET("orderneedship")
    Observable<OrderModel> getOrderNeedShip(@Query("key") String apiKey,
                                            @Query("restaurantId") int restaurantId);

    @GET("token")
    Observable<TokenModel> getToken(@Query("key") String apiKey,
                                    @Query("fbid") String fbid);

    @POST("token")
    @FormUrlEncoded
    Observable<TokenModel> updateToken(@Field("key") String apiKey,
                                       @Field("fbid") String fbid,
                                       @Field("token") String token);

    @GET("orderdetailbyrestaurant")
    Observable<OrderDetailModel> getOrderDetailModel(@Query("key") String apiKey,
                                                     @Query("orderId") int orderId,
                                                     @Query("restaurantId") int restaurantId);

    @GET("shippingorderbyshipper")
    Observable<ShippingOrderModel> getShippingOrder(@Query("key") String apiKey,
                                                    @Query("restaurantId") int restaurantId,
                                                    @Query("shipperId") String shipperId);

    @GET("orderofshipper")
    Observable<OrderModel> getOrderOfShipper(@Query("key") String apiKey,
                                                    @Query("shipperId") String shipperId);

    @POST("shippingorder")
    @FormUrlEncoded
    Observable<ShippingOrderModel> setShippingOrder(@Field("key") String apiKey,
                                                    @Field("orderId") int orderId,
                                                    @Field("restaurantId") int restaurantId,
                                                    @Field("shipperId") String shipperId,
                                                    @Field("status") int status);

    @PUT("updateOrder")
    @FormUrlEncoded
    Observable<UpdateOrderModel> updateOrderStatus(@Field("key") String apiKey,
                                                   @Field("orderId") int orderId,
                                                   @Field("orderStatus") int orderStatus);


    /*@PUT("updatemenu")
    @FormUrlEncoded
    Observable<MenuModel> updateMenu(@Field("key") String apiKey,
                                     @Field("menuId") int menuId,
                                     @Field("name") String name,
                                     @Field("description") String description,
                                     @Field("image") String image);

    @DELETE("favorite")
    Observable<FavoriteModel> removeFavorite(@Query("key") String apiKey,
                                             @Query("fbid") String fbid,
                                             @Query("foodId") int foodId,
                                             @Query("restaurantId") int restaurantId);*/
}
