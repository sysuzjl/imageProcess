% 快速引导滤波器图像增强的测试例子
% 存储图像路径
str='./image';
t1=clock;
fastguided_ehancement(strcat(str,'/tulips.bmp'), strcat(str,'/image/ehancement/fastguided_tulips'));
fastguided_ehancement(strcat(str,'/tomato.bmp'), strcat(str,'/image/ehancement/fastguided_tomato'));
fastguided_ehancement(strcat(str,'/bird.bmp'), strcat(str,'/image/ehancement/fastguided_bird'));
fastguided_ehancement(strcat(str,'/monarch.bmp'), strcat(str,'/image/ehancement/fastguided_monarch'));
fastguided_ehancement(strcat(str,'/starynight.bmp'), strcat(str,'/image/ehancement/fastguided_starynight'));
t2=clock;
disp(etime(t2,t1));