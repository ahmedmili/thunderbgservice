Pod::Spec.new do |s|
  s.name              = 'ThunderBgService'
  s.version           = '0.1.0'
  s.summary           = 'Capacitor foreground service (iOS stub)'
  s.license           = { :type => 'MIT' }
  s.homepage          = 'https://example.com/webify/capacitor-thunder-bg-service'
  s.author            = 'Webify'
  s.source            = { :git => 'https://example.com/webify/capacitor-thunder-bg-service.git', :tag => s.version.to_s }
  s.source_files      = 'Plugin/**/*.{swift,h,m}'
  s.ios.deployment_target = '13.0'
  s.dependency 'Capacitor', '~> 7.0'
end


