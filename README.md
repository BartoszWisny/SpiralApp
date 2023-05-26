<img src="https://github.com/BartoszWisny/SpiralApp/blob/master/app/src/main/res/drawable/spiral_logo.png" align="right" style="margin:5px;width:200px;height:200px"/>

# SpiralApp

Real time chat app project hosted on Google's [Firebase](https://firebase.google.com/). 

### What can you do in Spiral?
User can log in directly using Google Account or use e-mail address to sign up. After successful verification, user is able to chat with other users using individual chat rooms. App supports sending pictures (from camera or gallery), audio recordings and text messages. If needed, user has the option to change their profile data and profile picture.

### Supported versions of Android and language versions
To use the app, Android 11 (version code R, of API level at least 30) or higher is required. Supported languages: English, Polish.

### Used libraries
* Firebase related libraries,
* [Audiogram](https://github.com/alxrm/audiowave-progressbar) by alxrm,
* [AudioRecordView](https://github.com/Armen101/AudioRecordView) by Armen101,
* [Picasso](https://github.com/square/picasso) by Square,
* [Retrofit](https://github.com/square/retrofit) by Square,
* [Shape Image View](https://github.com/siyamed/android-shape-imageview) by siyamed,
* [TouchImageView](https://github.com/MikeOrtiz/TouchImageView) by MikeOrtiz,
* [TedPermission](https://github.com/ParkSangGwon/TedPermission) by Ted Park,
* [RxJava](https://github.com/ReactiveX/RxJava).

### Screenshots
* Dark Mode
<div>
  <img src="https://private-user-images.githubusercontent.com/91944872/241225664-90305a75-2f33-47aa-976b-dea322c1e6e4.jpg?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrZXkiOiJrZXkxIiwiZXhwIjoxNjg1MTA2MjM0LCJuYmYiOjE2ODUxMDU5MzQsInBhdGgiOiIvOTE5NDQ4NzIvMjQxMjI1NjY0LTkwMzA1YTc1LTJmMzMtNDdhYS05NzZiLWRlYTMyMmMxZTZlNC5qcGc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBSVdOSllBWDRDU1ZFSDUzQSUyRjIwMjMwNTI2JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDIzMDUyNlQxMjU4NTRaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0yNDU1MGExYmRiOGYwMzc3OGE2OTA0ODExY2ZiNWQxMGYxMzdhYjEyMGZmZTM1NjlmNWZkMzVhNGY1NjcyODkzJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.2ubc4F4TvA-LQRsl2pVLjDoz9s-9VLBXvIkSeiclkTI" style="height:627px;width:297px"/>
  <img src="https://private-user-images.githubusercontent.com/91944872/241225633-99b5ef09-9f2c-411b-9b77-73a7e903a345.jpg?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrZXkiOiJrZXkxIiwiZXhwIjoxNjg1MTA2MjM0LCJuYmYiOjE2ODUxMDU5MzQsInBhdGgiOiIvOTE5NDQ4NzIvMjQxMjI1NjMzLTk5YjVlZjA5LTlmMmMtNDExYi05Yjc3LTczYTdlOTAzYTM0NS5qcGc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBSVdOSllBWDRDU1ZFSDUzQSUyRjIwMjMwNTI2JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDIzMDUyNlQxMjU4NTRaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT0wYTFhMjhmNjE0Y2NmYzNkM2MxNjk4MDhiOGRlN2VmYWM2ZjgzZGU4YjIxNzVkYTVlZjI1MDIzM2RhZGFkNDZmJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.0480wr0V1ApMkwKkIRlMsaCQLMzLBxOfmXsbhnBX-cU" style="height:627px;width:297px"/>
  <img src="https://private-user-images.githubusercontent.com/91944872/241225652-62f28413-e243-4ae2-b80d-4d320cd7848b.jpg?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrZXkiOiJrZXkxIiwiZXhwIjoxNjg1MTA2MjM0LCJuYmYiOjE2ODUxMDU5MzQsInBhdGgiOiIvOTE5NDQ4NzIvMjQxMjI1NjUyLTYyZjI4NDEzLWUyNDMtNGFlMi1iODBkLTRkMzIwY2Q3ODQ4Yi5qcGc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBSVdOSllBWDRDU1ZFSDUzQSUyRjIwMjMwNTI2JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDIzMDUyNlQxMjU4NTRaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT04N2MzY2Q2OWQxOGYxOTRmYzNmMDY4NjI1ZWIwYzlmNzIyNzVlYjE1MzA2YjQ3OGE5MmUxNzU5Y2ExNWU1M2Y2JlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.A3l5wUtKiS-LA6Rs3jmKfPWRippdFzCyCILtl0qUZFk" style="height:627px;width:297px"/>
</div>
<div>
  <img src="https://private-user-images.githubusercontent.com/91944872/241225601-a864b2ea-b09d-457b-be31-79c1f009535f.jpg?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrZXkiOiJrZXkxIiwiZXhwIjoxNjg1MTA2MjM0LCJuYmYiOjE2ODUxMDU5MzQsInBhdGgiOiIvOTE5NDQ4NzIvMjQxMjI1NjAxLWE4NjRiMmVhLWIwOWQtNDU3Yi1iZTMxLTc5YzFmMDA5NTM1Zi5qcGc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBSVdOSllBWDRDU1ZFSDUzQSUyRjIwMjMwNTI2JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDIzMDUyNlQxMjU4NTRaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1iZDBkNzllZjFiMGFmZDMwMThkMTBjNjcxYjI5MmNiMTJhMWFlMDFkZDIwNjczOTg0ZThlNWJmNDJhMmNmYTdiJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.81xTDGCkVnQtJBmssTRfqsBuZaL7nhXaaYnSrA0EnjQ" style="height:627px;width:297px"/>
  <img src="https://private-user-images.githubusercontent.com/91944872/241225620-3119c8b3-6fad-4a0d-b238-68484e76562d.jpg?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJrZXkiOiJrZXkxIiwiZXhwIjoxNjg1MTA2MjM0LCJuYmYiOjE2ODUxMDU5MzQsInBhdGgiOiIvOTE5NDQ4NzIvMjQxMjI1NjIwLTMxMTljOGIzLTZmYWQtNGEwZC1iMjM4LTY4NDg0ZTc2NTYyZC5qcGc_WC1BbXotQWxnb3JpdGhtPUFXUzQtSE1BQy1TSEEyNTYmWC1BbXotQ3JlZGVudGlhbD1BS0lBSVdOSllBWDRDU1ZFSDUzQSUyRjIwMjMwNTI2JTJGdXMtZWFzdC0xJTJGczMlMkZhd3M0X3JlcXVlc3QmWC1BbXotRGF0ZT0yMDIzMDUyNlQxMjU4NTRaJlgtQW16LUV4cGlyZXM9MzAwJlgtQW16LVNpZ25hdHVyZT1hOThiYjgwYTI4NjE0ZjEyZGY3ODE1ZDA0NGZmZmZkYTYwODUyNzYwNmMzZGZhOTI4NWIzZWM5ZTBlMmNiNWQxJlgtQW16LVNpZ25lZEhlYWRlcnM9aG9zdCJ9.wVaPJGUHNYCPRBnIPdEPTYRcKbKN9nBo_16PuC9fglM" style="height:627px;width:297px"/>
</div>

### Authors
Bartosz Wiśny and Marek Świergoń, 2023.
