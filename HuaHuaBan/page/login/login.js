var app = getApp()
Page({

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

  data: {
    userName: '',
    id_token: '',//方便存在本地的locakStorage  
    response: '' //存取返回数据  
  },
  userNameInput: function (e) {
    this.setData({
      userName: e.detail.value
    })
    console.log(this.data.userName)
  },

  logIn: function () {
    var that = this
    wx.request({
      url: 'https://78413126.draw3dsketch.com/login',
      data: {
        drawerid: this.data.userName,
      },
      method: 'GET',
      success: function (res) {
        that.setData({
          id_token: res.data.id_token,
          response: res
        })
        try {
          wx.setStorageSync('id_token', res.data.id_token)//同步存储
          wx.setStorageSync('pic_url',res.data.resource_array)
        } catch (e) {
        }

        console.log(res.data);
        wx.navigateTo({
          url: '../index/index'
        })
      },
      fail: function (res) {
        console.log(res.data);
        console.log('is failed')
      }
    })
 }
})  