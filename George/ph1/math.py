from scipy import integrate
from scipy.stats import norm 

def one(x) : 

    return x*x 

"""
Returns the normal cdf from the value of x.
"""
def normpdf(x, std) :

    return norm.pdf(x, loc=0, scale=std)

""" 
There would be r bands. Hence r bandlengths would be returned
by the function.
"""
def getBands(r, std) :

    bands = list()
    denom = integrate.quad(normpdf, 0, 1, args=0.25)
    for i in range(1, r + 1) :
	xmin = (i-1)*1.0/r
        xmax = i*1.0/r  
        print "LOG i, x, y", i, xmin, xmax
        numer = integrate.quad(normpdf, xmin, xmax, args=0.25)
        length = numer[0]/denom[0]
        bands.append(length)

    bands.sort()
    return bands


bands = getBands(16, 0.25)
print bands
print "Total Length =" , len(bands)
