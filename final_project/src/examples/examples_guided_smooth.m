% 普通引导滤波器图像平滑的测试例子
% 存储图像路径
str='./image';
t1=clock;
guidedfilter_smoothing(strcat(str,'/boy.bmp'), strcat(str,'/image/smooth/boy'));
guidedfilter_smoothing(strcat(str,'/cat.bmp'), strcat(str,'/image/smooth/cat'));
guidedfilter_smoothing(strcat(str,'/lena.bmp'), strcat(str,'/image/smooth/lena'));
guidedfilter_smoothing(strcat(str,'/baboon.bmp'), strcat(str,'/image/smooth/baboon'));
guidedfilter_smoothing(strcat(str,'/beauty_with_freckle.bmp'), strcat(str,'/image/smooth/beauty_with_freckle'));
t2=clock;
disp(etime(t2,t1));