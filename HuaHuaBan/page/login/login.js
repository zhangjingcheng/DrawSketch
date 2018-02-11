var app = getApp()
Page({

  /**
 * 生命周期函数--监听页面加载
 */
  onLoad() {
    wx.getSystemInfo({
      success: function (res) {
        wx.setStorageSync('displayx', res.screenWidth)
        wx.setStorageSync('displayy', res.screenHeight)
        wx.setStorageSync('device',res.model)
        wx.setStorageSync('pixelRatio', res.pixelRatio)
        console.log(res)
      }
    })
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
      url: 'https://wxapi.hotapp.cn/proxy/?appkey=hotapp403228604&url=https://78413126.draw3dsketch.com/login',
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
          wx.setStorageSync('drawerid', that.data.userName)
          wx.setStorageSync('id_token', res.data.id_token)//同步存储
          wx.setStorageSync('pic_url', res.data.resource_array)
          wx.setStorageSync('un_count', res.data.unfinished_count)
          wx.setStorageSync('method', 1)
          console.log(res.data);
        } catch (e) {
          console.log(e)
        }

        console.log(wx.getStorageSync('pic_url'));
        if (res.data.result == 1){

          wx.showModal({
            title: '提示',
            content: '剩余' + res.data.unfinished_count + '张',
            success: function (res) {
              if (res.confirm) {
                console.log('用户点击确定');
                wx.navigateTo({
                  url: '../show/show_image'
                })
              }
            }
          });  
         

     }

        if (res.data.result == -1) {
          wx.showModal({
            title: '提示',
            content: '用户名错误',
            success: function (res) {
              if (res.confirm) {
                console.log('用户点击确定');
              }
            }
          })
        }
      
      },
      fail: function (res) {
        console.log(res.data);
        console.log('is failed');
        wx.showModal({
          title: '提示',
          content: '未连接到服务器',
          success: function (res) {
            if (res.confirm) {
              console.log('用户点击确定');
            }
          }
        })
      }
    })
 }
})  