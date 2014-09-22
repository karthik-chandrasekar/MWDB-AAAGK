function  [band_lengths] = quantization(r)
i = 1;
denom = integral(@(y)normpdf(y,0,0.25),0,1);
band_lengths = zeros(1,r);
while(i <= r)
    xmin = (i-1)/r;
    xmax = i/r;
    fun = @(x)normpdf(x,0,0.25);
    numer = integral(fun,xmin,xmax);
    band_lengths(i) = numer/denom;
    i = i+1;
end;
disp(band_lengths);
