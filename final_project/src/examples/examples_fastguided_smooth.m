% 快速引导滤波器图像平滑的测试例子
% 存储图像路径
str='./image';
t1=clock;
fastguided_smoothing(strcat(str,'/boy.bmp'), strcat(str,'/image/smooth/fastguided_boy'));
fastguided_smoothing(strcat(str,'/cat.bmp'), strcat(str,'/image/smooth/fastguided_cat'));
fastguided_smoothing(strcat(str,'/lena.bmp'), strcat(str,'/image/smooth/fastguided_lena'));
fastguided_smoothing(strcat(str,'/baboon.bmp'), strcat(str,'/image/smooth/fastguided_baboon'));
fastguided_smoothing(strcat(str,'/beauty_with_freckle.bmp'), strcat(str,'/image/smooth/fastguided_beauty_with_freckle'));
t2=clock;
disp(etime(t2,t1));