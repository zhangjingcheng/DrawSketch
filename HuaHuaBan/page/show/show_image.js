const app = getApp();

Page({

  /**
     * 页面名称
     */
  name: "index",
  /**
   * 页面的初始数据
   */
  data:{
    imageUrl:'',
  },
  /**
   * 生命周期函数--监听页面加载
   */
  onLoad() {
   
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady() {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow() {
    this.initImages();
    //等待10s
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide() {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload() {

  },

  initImages(){
    var that = this;
    wx.getStorage({
    key: 'imageUrl',
    success: function (res) {
      console.log(res.data)
      that.setData({ imageUrl: res.data })
    }
  })
    
  },

  goBack: function () {
    var that = this;
    wx.redirectTo({
      url: '../index/index',
    })
  },

})