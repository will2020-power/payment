<template>
<!--中间内容-->
  <div class="pay-main">
    <div class="pay-container">
      <div class="checkout-tit">
        <h4 class="tit-txt">
          <span class="success-icon"></span>
          <span class="success-info">模拟订单提交成功</span>
        </h4>
        <div class="paymark">
          <span class="fl">订单号：<em>{{ payInfo.orderNo }}</em></span>
          <span class="fr"><em class="lead">应付金额：</em><em class="orange money">￥{{ payInfo.totalFee }}元</em></span>
        </div>
      </div>
      <div class="checkout-steps">
        <div class="step-tit">
          <h5>请选择支付平台(支持支付宝和微信支付)</h5>
        </div>
        <div class="step-cont">
          <ul class="payType">
            <li :class="{ 'selected': selectedPayType === 'alipay', 'disabled': selectedPayType === 'wechat' }" @click="selectPayType('alipay')"><img src="@/assets/images/pay2.jpg"></li>
            <li :class="{ 'selected': selectedPayType === 'wechat', 'disabled': selectedPayType === 'alipay' }" @click="selectPayType('wechat')"><img src="@/assets/images/pay3.jpg"></li>
          </ul>

        </div>
        <div class="hr"></div>

        <div class="submit">
          <a class="btn" href="javascript:" @click="pay">立即支付</a>
        </div>
      </div>
    </div>
    <DialogComponent
        :isShow="isShow"
        title="扫码支付"
        cancelText="取消支付"
        okText="支付成功"
        @hidden="cancelPay"
        @ok="paySuccess"
        @cancel="cancelPay"
    >
      <div class="main">
        <!--        <img :src="qrCodeUrl"/>-->
        <div id="pay_qrcode">
          <iframe id="frame" class="pay-iframe" :src="alipay" frameborder="0"></iframe>
        </div>
      </div>
    </DialogComponent>
  </div>
</template>

<script>

import {reqPay} from '@/api/pay'
import moment from 'moment'

export default {
    name: 'pay-page',
    data() {
        return {
            isShow: false,
            payInfo: {totalFee: 0.02, orderNo: `${moment().format('YYYYMMDDHHmmss')}`},
            tradeStatus: '',
            alipay: '',
            selectedPayType: 'wechat'
        }
    },
    methods: {
        async pay() {
            this.isShow = true
            this.alipay = process.env.VUE_APP_API_HOST + '/' + this.selectedPayType + '/' + this.payInfo.orderNo
            this.timer = setInterval(async () => {
                const {tradeStatus} = await reqPay(this.selectedPayType+'/'+this.payInfo.orderNo)
                console.log(tradeStatus)
                this.tradeStatus = tradeStatus
                if (tradeStatus === 'success') {
                this.$router.push('/paySuccess')
                }
            }, 2000)
        },
        // 支付成功
        paySuccess() {
        // code为200则支付成功  为205则未支付
        if (this.tradeStatus === 'success') {
            // 跳转支付成功页
            this.$router.push('/paySuccess')
        } else {
            // 提示未支付
            alert('未支付')
        }
        },
        // 取消支付
        cancelPay() {
        // 1. 关闭定时器
        this.timer && clearInterval(this.timer)
        // 2. 关闭支付框
        this.isShow = false
        },
        selectPayType(type) {
        console.log(type)
        this.selectedPayType = type
    }
    }
}
</script>

<style lang="scss" scoped>
//中间内容
.pay-main {
  margin-bottom: 20px;

  .pay-container {
    margin: 0 auto;
    width: 1200px;

    a:hover {
      color: #4cb9fc;
    }

    .orange {
      color: #e1251b;
    }

    .money {
      font-size: 18px;
    }

    .zfb {
      color: #e1251b;
      font-weight: 700;
    }

    .checkout-tit {
      padding: 10px;

      .tit-txt {
        font-size: 14px;
        line-height: 21px;

        .success-icon {
          width: 30px;
          height: 30px;
          display: inline-block;
          background: url(@/assets/images/icon.png) no-repeat 0 0;
        }

        .success-info {
          padding: 0 8px;
          line-height: 30px;
          vertical-align: top;
        }
      }

      .paymark {
        overflow: hidden;
        line-height: 26px;
        text-indent: 38px;

        .fl {
          float: left;
        }

        .fr {
          float: right;

          .lead {
            margin-bottom: 18px;
            font-size: 15px;
            font-weight: 400;
            line-height: 22.5px;
          }
        }
      }
    }

    .checkout-info {
      padding-left: 25px;
      padding-bottom: 15px;
      margin-bottom: 10px;
      border: 2px solid #e1251b;

      h4 {
        margin: 9px 0;
        font-size: 14px;
        line-height: 21px;
        color: #e1251b;
      }

      ol {
        padding-left: 25px;
        list-style-type: decimal;
        line-height: 24px;
        font-size: 14px;
      }

      ul {
        padding-left: 25px;
        list-style-type: disc;
        line-height: 24px;
        font-size: 14px;
      }
    }

    .checkout-steps {
      border: 1px solid #ddd;
      padding: 25px;

      .hr {
        height: 1px;
        background-color: #ddd;
      }

      .step-tit {
        line-height: 36px;
        margin: 15px 0;
      }

      .step-cont {
        margin: 0 10px 12px 20px;

        ul {
          font-size: 0;

          li {
            margin: 2px;
            display: inline-block;
            padding: 5px 20px;
            border: 1px solid #ddd;
            cursor: pointer;
            line-height: 18px;
          }

          .selected {
            border-color: #77b72c;
            border-width: 3px;
          }

          .disabled {
            -webkit-filter: grayscale(100%);
            -moz-filter: grayscale(100%);
            -ms-filter: grayscale(100%);
            -o-filter: grayscale(100%);
            filter: grayscale(100%);
            filter: gray;
          }
        }
      }
    }

    .submit {
      text-align: center;

      .btn {
        display: inline-block;
        padding: 15px 45px;
        margin: 15px 0 10px;
        font: 18px "微软雅黑";
        font-weight: 700;
        border-radius: 0;
        background-color: #e1251b;
        border: 1px solid #e1251b;
        color: #fff;
        text-align: center;
        vertical-align: middle;
        cursor: pointer;
        user-select: none;
        text-decoration: none;
      }
    }
  }
}

.main {
  text-align: center;

  img {
    width: 200px;
  }
}

.pay_qrcode {
  width: 100%;
  height: 100%;
}

.pay-iframe {
  width: 210px;
  height: 210px;
}

</style>