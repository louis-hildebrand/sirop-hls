.PHONY: run-fpga
run-fpga: fpga
	./sobel

.PHONY: synth
synth: fpga
	# Update .sdc file
	sed -e 's/clk/clock/g' ../../../../src/main/resources/mhir/gen/top.sdc > ./sobel.prj/quartus/quartus_compile.sdc
	# Update .qsf file
	echo -e "\n\n# Synthesis settings used by Sirop" >> ./sobel.prj/quartus/quartus_compile.qsf
	grep -Ev '^set_global_assignment -name (FAMILY|DEVICE|TOP_LEVEL_ENTITY)' ../../../../src/main/resources/mhir/gen/top.qsf >> ./sobel.prj/quartus/quartus_compile.qsf
	# Invoke Quartus
	cd ./sobel.prj/quartus/ && quartus_sh --flow compile quartus_compile

.PHONY: fpga
fpga: sobel.prj

sobel.prj: sobel.cpp
	i++ -march='10AX115N2F40E2LG' -ghdl --clock 175MHz ./sobel.cpp -o sobel

.PHONY: run-cpu
run-cpu: cpu
	./sobel-cpu

.PHONY: cpu
cpu: sobel-cpu

sobel-cpu: sobel.cpp
	i++ -march='x86-64' ./sobel.cpp -o sobel-cpu

.PHONY: clean
clean:
	rm -rf sobel.prj/ sobel sobel-cpu transcript
