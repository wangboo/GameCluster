require 'fileutils'
require 'json'
require 'digest/md5'

buildfile = ".pb_build_info"
bin = "./protoc.exe"

def md5 filepath 
	Digest::MD5.hexdigest File.read(filepath)
end

def has_args? l 
	(l - ARGV).size < l.size 
end

if has_args? %w{help -h --help -help}
	puts "--------------编译protobuf到java项目---------------"
	puts "ruby proto2java.rb 默认编译有改动的文件"
	puts "ruby proto2java.rb all 强制编译所有文件"
	puts "ruby proto2java.rb up 更新protobuf svn资源"
	puts "ruby proto2java.rb ci 提交protobuf 到svn"
	exit
end

if has_args? %w(-up up) then %x(cd ../proto && svn up); exit end
if has_args? %w(-ci ci) then %x(cd ../proto && svn ci -m "proto ci"); exit end

filepaths = Dir.glob("./proto/*.proto")
filenames = filepaths.map{|s|s.match(/^.*\/(\w+\.proto)$/)[1]}
# puts filenames
data = if File.exists? buildfile then JSON.parse(File.read(buildfile)) else {} end
diff = if ARGV.include? "all" 
	filepaths.zip(filenames)
else 
	filepaths
		.zip(filenames)
		.drop_while{|path, name| data[name] and data[name] == md5(path)}
end
if diff.empty? then puts "Not file changed, EXIT!"; exit end 
fileargs = diff
	.map{|path, name| path }
	.join(" ")
diff.reduce(data){|s, z| s[z.last] = md5(z.first); s}
# puts "fileargs = #{fileargs}"
exec = "#{bin} --java_out=./src --proto_path=./proto/ #{fileargs}"
puts exec
out = %x(#{exec})
## 写更新
File.open(buildfile, "w"){|f|f.write(data.to_json)} if out.size == 0

