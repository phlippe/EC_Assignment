from glob import glob
import numpy as np 
import matplotlib.pyplot as plt
import os


FOLDER = sorted(glob("*/island_*/")) + sorted(glob("*/"))

for fold in FOLDER:
	print("Processing " + fold)
	for text_file in sorted(glob(fold + "*.txt")):
		if os.path.isfile(text_file.replace(".txt",".png")):
			continue
		elif text_file.split("/")[-1].startswith("fitness"):
			if os.path.isfile(text_file.rsplit("/",1)[0] + "/fitness_min.png"):
				continue
		elif text_file.split("/")[-1].startswith("position"):
			continue
		elif text_file.split("/")[-1].startswith("island_exchange"):
			continue
		with open(text_file, "r") as f:
			try:
				numbers = [float(l) for l in f.readlines()]
			except ValueError:
				numbers = [[float(x) for x in l.split(",")] for l in f.readlines()]
				continue
		plt.plot(numbers)
		title = text_file.split("/")[-1].split(".")[0]
		if title == "fitness_max" or title == "fitness_mean":
			continue
		plt.title(title)
		plt.xlabel("Number cycles")
		plt.ylabel("Fitness" if title.startswith("fitness") else title)
		if title.startswith("multi_sigma"):
			plt.yscale('logit')
		plt.savefig(text_file.replace(".txt",".png"))
		plt.close()

