import numpy as np 
from glob import glob
import math
import matplotlib.pyplot as plt
from matplotlib import cm 
from mpl_toolkits.mplot3d import Axes3D

func_files = sorted(glob("katsuura*.txt"))
for file_name in func_files:
	with open(file_name) as f:
		fitness = [float(line) for line in f.readlines() if line != "\n"]
	fitness_z = np.array(fitness, dtype=np.float32)
	print max(fitness)
	number_steps = int(math.sqrt(len(fitness)))
	fitness_z = fitness_z.reshape((number_steps, number_steps))
	x = np.linspace(0, 1, number_steps)
	y = np.linspace(0, 1, number_steps)
	xv, yv = np.meshgrid(x, y)

	fig = plt.figure()
	ax = fig.gca(projection='3d')
	surf = ax.plot_surface(xv, yv, fitness_z, cmap=cm.coolwarm,linewidth=0,antialiased=False)
	fig.colorbar(surf, shrink=0.5, aspect=5)
	plt.savefig(file_name.replace(".txt",".png"))
	plt.close()
