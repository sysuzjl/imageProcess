% 普通引导滤波器图像增强的测试例子
% 存储图像路径
str='./image';
t1=clock;
guidedfilter_ehancement(strcat(str,'/bird.bmp'), strcat(str,'/image/ehancement/bird'));
guidedfilter_ehancement(strcat(str,'/tomato.bmp'), strcat(str,'/image/ehancement/tomato'));
guidedfilter_ehancement(strcat(str,'/tulips.bmp'), strcat(str,'/image/ehancement/tulips'));
guidedfilter_ehancement(strcat(str,'/monarch.bmp'), strcat(str,'/image/ehancement/monarch'));
guidedfilter_ehancement(strcat(str,'/starynight.bmp'), strcat(str,'/image/ehancement/starynight'));
t2=clock;
disp(etime(t2,t1));
